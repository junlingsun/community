package com.junling.comunity.service;

import com.junling.comunity.dao.TicketDao;
import com.junling.comunity.dao.UserDao;
import com.junling.comunity.entity.LoginTicket;
import com.junling.comunity.entity.User;
import com.junling.comunity.utility.CommunityUtil;
import com.junling.comunity.utility.Constant;
import com.junling.comunity.utility.EmailUtil;
import com.junling.comunity.utility.RedisKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

@Service
public class UserServiceImp implements UserService, Constant {
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private TicketDao ticketDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public User findUserById(int id) {

        User user = getCatche(id);
        if (user == null) {
            user = initCatche(id);
        }
        return user;
    }

    @Override
    public User findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    @Override
    public Map<String, Object> saveUser(User user) {

        Map<String, Object> msg = new HashMap<>();

        String username = user.getUsername();
        String email = user.getEmail();

        if (StringUtils.isBlank(username)) {
            msg.put("usernameMsg", "username cannot be empty");
            return msg;
        }

        if (StringUtils.isBlank(email)) {
            msg.put("emailMsg", "email cannot be empty");
            return msg;
        }

        if(StringUtils.isBlank(user.getPassword())) {
            msg.put("passwordMsg", "password cannot be empty");
            return msg;
        }

        User searchUser = userService.findUserByUsername(username);
        if (searchUser != null) {
            msg.put("usernameMsg", "The input username exists");
            return msg;
        }
        searchUser = userService.findUserByEmail(email);
        if (searchUser != null) {
            msg.put("emailMsg", "The input email exists");
            return msg;
        }


        //user register
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setCreateTime(new Date());
        user.setHeaderUrl("#");
        user.setSalt(CommunityUtil.generateUUID().substring(5));
        user.setPassword(CommunityUtil.generatePassword(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        userDao.saveUser(user);




        //email to activate new user account
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = "localhost:8080/user/activation/" + user.getActivationCode() + "/" + user.getId();
        context.setVariable("url", url);
        String content = templateEngine.process("mail/activation", context);
        emailUtil.sendEmail(user.getEmail(), "account activation", content);


        return msg;
    }

    @Override
    public int accountActivation(String code, int id) {
        User user = findUserById(id);
        String activationCode = user.getActivationCode();
        int status = user.getStatus();


        if (StringUtils.equals(code, activationCode)){
            if (status == 0) {
                userDao.updateUserStatus(id,1);
                clearCatche(id);
                return ACTIVATION_SUCCESS;
            }else {
                return ACTIVATION_REPEAT;
            }
        }


        return ACTIVATION_FAIL;
    }

    @Override
    public Map<String, Object> login(String username, String password, int expired) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "username cannot be empty");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("password", "password cannot be empty");
            return map;
        }


        User user = userDao.findUserByUsername(username);

        if (user == null) {
            map.put("usernameMsg", "username doesn't exist");
            return map;
        }

        password = CommunityUtil.generatePassword(password+user.getSalt());

        String pd = user.getPassword();
        if (!StringUtils.equals(pd, password)) {
            map.put("passwordMsg", "username and password do not match");
            return map;
        }

        LoginTicket ticket = new LoginTicket();
        ticket.setTicket(CommunityUtil.generateUUID());
        ticket.setStatus(0);
        ticket.setUserId(user.getId());
        ticket.setExpired(new Date(System.currentTimeMillis()+expired*1000));

//        ticketDao.saveTicket(ticket);
        String key = RedisKey.getTicketKey(ticket.getTicket());
        redisTemplate.opsForValue().set(key, ticket);


        map.put("ticket", ticket.getTicket());
        return map;
    }

    @Override
    public void logout(int status, String ticket) {

        String key= RedisKey.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(key);
        loginTicket.setStatus(status);



        redisTemplate.opsForValue().set(key, loginTicket);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        String key= RedisKey.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(key);
        return loginTicket;
    }

    @Override
    public Map<String, Object> passwordUpdate(User user, String oldPassword, String newPassword) {
        Map<String, Object> map = new HashMap<>();

        String password = user.getPassword();
        oldPassword = CommunityUtil.generatePassword(oldPassword + user.getSalt());

        if (!StringUtils.equals(password, oldPassword)) {
            map.put("errorMsg", "password is incorrect");
            return map;
        }
        newPassword = CommunityUtil.generatePassword(newPassword+user.getSalt());

        userDao.updatePassword(user.getId(), newPassword);
        clearCatche(user.getId());
        return map;
    }

    @Override
    public void updateHeaderUrl(User user, String headerUrl) {
        userDao.updateHeaderUrl(user.getId(), headerUrl);
        clearCatche(user.getId());
    }


    //get user from catche if it's already in the catche
    private User getCatche(int userId) {
        String key = RedisKey.getUserKey(userId);


        return (User)redisTemplate.opsForValue().get(key);
    }

    //if not in the catche, init it from database
    private User initCatche(int userId) {
        String key = RedisKey.getUserKey(userId);
        User user = userDao.findUserById(userId);
        redisTemplate.opsForValue().set(key, user);
        return user;
    }

    //after update, clear from catche
    private void clearCatche(int userId) {
        String key = RedisKey.getUserKey(userId);
        redisTemplate.delete(key);
    }

    /*@Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = this.findUserByUsername(s);

        return user;
    }*/



    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = userDao.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {

                if (user.getType() == 1) {
                    return AUTHORITY_ADMIN;
                }else if (user.getType() == 2) {
                    return AUTHORITY_MODERATOR;
                }else {
                    return AUTHORITY_USER;
                }

            }
        });

        return list;
    }
}
