package com.junling.comunity.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.junling.comunity.entity.User;
import com.junling.comunity.service.FollowService;
import com.junling.comunity.service.LikeService;
import com.junling.comunity.service.UserService;
import com.junling.comunity.utility.CommunityUtil;
import com.junling.comunity.utility.Constant;
import com.junling.comunity.utility.HostHolder;
import com.junling.comunity.utility.RedisKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements Constant {

    @Autowired
    UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/register")
    public String register(User user, Model model){

        Map<String,Object> msg = userService.saveUser(user);

        model.addAttribute("usernameMsg", msg.get("usernameMsg"));
        model.addAttribute("emailMsg", msg.get("emailMsg"));
        model.addAttribute("passwordMsg", msg.get("passwordMsg"));

        System.out.println(msg.size());
        if (msg!=null && msg.size()!=0) {

            return "site/register";
        }

        model.addAttribute("msg", "a activation letter has been mailed to you");
        model.addAttribute("url", "/index");

        return "site/operate-result";

    }

    @GetMapping("/activation/{code}/{id}")
    public String activation(@PathVariable("code") String code, @PathVariable("id") int id, Model model) {
        int status = userService.accountActivation(code, id);

        if (status == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "account is successfully activated");
            model.addAttribute("url", "/loginPage");
        }else if (status == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "account has been activated");
            model.addAttribute("url", "/loginPage");
        }else {
            model.addAttribute("msg", "account cannot be activated");
            model.addAttribute("url", "/index");
        }
        return "site/operate-result";
    }

    @PostMapping("/login")
    public String login(String username, String password, String code, boolean rememberMe,
                       Model model, HttpServletResponse response, @CookieValue("label") String label){

        String kaptcha = null;
        if (StringUtils.isNotBlank(label)) {
            String key = RedisKey.getKaptchaKey(label);
            kaptcha = (String)redisTemplate.opsForValue().get(key);
        }



        if (StringUtils.isBlank(code) || !StringUtils.equals(kaptcha, code)) {
            model.addAttribute("kaptchaMsg", "verify code is incorrect");
            return "site/login";
        }

        int expiredSeconds = rememberMe? REMEMBER_EXPIRED_SECOND: DEFAULT_EXPIRED_SECOND;
        Map<String, Object> msg = userService.login(username, password, expiredSeconds);

        if (msg.containsKey("ticket")) {

            Cookie cookie = new Cookie("ticket", (String)msg.get("ticket"));
            cookie.setMaxAge(expiredSeconds);
            cookie.setPath("/");
            response.addCookie(cookie);
            return "redirect:/index";
        }else {

            model.addAttribute("usernameMsg", msg.get("usernameMsg"));
            model.addAttribute("passwordMsg", msg.get("passwordMsg"));
            return "site/login";

        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(1, ticket);

        //clear authenties
        SecurityContextHolder.clearContext();
        return "redirect:/index";
    }

    @GetMapping("/settingPage")
    public String setting(){
        return "site/setting";
    }

    @PostMapping("/passwordUpdate")
    public String passwordUpdate(String oldPassword, String newPassword, Model model){
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.passwordUpdate(user, oldPassword, newPassword);

        if (map == null || map.size()==0) {
           return "redirect:/index";
        }
        model.addAttribute("errorMsg", map.get("errorMsg"));
        return "site/setting";

    }

    @PostMapping("/headerUpdate")
    public String headerUpdate(MultipartFile headerImg, Model model) {
        if (headerImg == null) {
            model.addAttribute("errorMsg", "image cannot be empty");
            return "site/setting";
        }

        String filename = headerImg.getOriginalFilename();
        String suffix = filename.substring(filename.indexOf("."));

        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("errorMsg", "image format is not acceptable");
            return "site/setting";
        }

        filename = CommunityUtil.generateUUID()+suffix;

        try {
            headerImg.transferTo(new File(uploadPath + "/" + filename));

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("upload failed");
        }

        User user = hostHolder.getUser();

        String headerUrl = domain + "/user/headerImg/" + filename;
        userService.updateHeaderUrl(user, headerUrl);
        return "redirect:/index";
    }

    @GetMapping("/headerImg/{filename}")
    public void getHeaderImg(@PathVariable("filename") String filename, HttpServletResponse response){

        File file = new File(uploadPath, filename);
        String suffix = filename.substring(filename.lastIndexOf("."));

        response.setContentType("image/" + suffix);

        try(OutputStream os = response.getOutputStream();
            FileInputStream fs = new FileInputStream(file);
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;

            while((b = fs.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @GetMapping("/profile/{userId}")
    public String profile(@PathVariable("userId") int userId, Model model){
        String followerKey = RedisKey.getFollowerKey(userId);
        String followeeKey = RedisKey.getFolloweeKey(userId);


        model.addAttribute("followerCount", followService.followerCount(userId));
        model.addAttribute("followeeCount", followService.followeeCount(userId));
        model.addAttribute("isFollowed", followService.hasFollowed(userId, hostHolder.getUser().getId()));

        int userLikeCount = likeService.userLikeCount(userId);
        model.addAttribute("userLikeCount", userLikeCount);
        model.addAttribute("user", userService.findUserById(userId));

        return "site/profile";
    }
}
