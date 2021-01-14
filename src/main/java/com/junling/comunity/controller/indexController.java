package com.junling.comunity.controller;


import com.alibaba.fastjson.JSONObject;
import com.junling.comunity.entity.Message;
import com.junling.comunity.entity.Post;
import com.junling.comunity.entity.User;
import com.junling.comunity.service.LikeService;
import com.junling.comunity.service.MessageService;
import com.junling.comunity.service.PostService;
import com.junling.comunity.service.UserService;
import com.junling.comunity.utility.Constant;
import com.junling.comunity.utility.HostHolder;
import com.junling.comunity.utility.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class indexController implements Constant {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;



    @GetMapping("/index")
    public String indexPage(Model model, PageUtil pageUtil){
        pageUtil.setPath("/index");
        pageUtil.setTotalRows(postService.getTotalRows(0));

        List<Post> posts = postService.posts(0, pageUtil.getRowsPerPage(), pageUtil.getOffset());
        List<Map<String, Object>> list = new ArrayList<>();

        for (Post post: posts) {
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            int userId = post.getUserId();
            User user = userService.findUserById(userId);
            map.put("user", user);
            long likeCount = likeService.likeCount(POST_TYPE, post.getId());
            map.put("likeCount", likeCount);
            list.add(map);
        }

        int unreadMessages = 0;
        if (hostHolder.getUser() != null) {
            unreadMessages = messageService.totalUnreadCount(hostHolder.getUser().getId()) + messageService.unread(hostHolder.getUser().getId(), null);
        }

        model.addAttribute("unreadMessages", unreadMessages);

        model.addAttribute("list", list);



        return "index";
    }

    @GetMapping("/registerPage")
    public String registerPage(){
        return "site/register";
    }

    @GetMapping("/loginPage")
    public String loginPage(){
        return "site/login";
    }

    @GetMapping("/messagePage")
    public String messagePage(Model model, PageUtil page){

        User user = hostHolder.getUser();
        page.setTotalRows(messageService.conversationCount(user.getId()));
        page.setRowsPerPage(5);
        page.setPath("/messagePage");
        List<Message> messages = messageService.messages(user.getId(), page.getOffset(), page.getRowsPerPage());
        List<Map<String, Object>> messageVOList = new ArrayList<>();
        for (Message message: messages) {
            Map<String, Object> messageVO = new HashMap<>();
            User fromUser = userService.findUserById(message.getFromId());
            messageVO.put("fromUser", fromUser);
            messageVO.put("message", message);
            messageVO.put("messageCount", messageService.messageCountPerConversation(user.getId(), message.getConversationId()));
            messageVO.put("unreadCount", messageService.unreadCountPerConversation(user.getId(), message.getConversationId()));
            messageVOList.add(messageVO);
        }

        model.addAttribute("totalLetterUnread", messageService.totalUnreadCount(user.getId()));
        model.addAttribute("totalNoticeUnread", messageService.unread(user.getId(), null));

        model.addAttribute("conversations", messageVOList);
        return "site/letter";
    }

    @GetMapping("/noticePage")
    public String noticePage(Model model){
        User user = hostHolder.getUser();



        Message message = messageService.lastestMessage(user.getId(), MESSAGE_TOPIC);
        Map<String, Object> messageVO = new HashMap<>();


        if (message != null) {
            messageVO.put("message",message);
            messageVO.put("unread", messageService.unread(user.getId(), MESSAGE_TOPIC));
            messageVO.put("count", messageService.count(user.getId(), MESSAGE_TOPIC));
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> map = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("fromUser", userService.findUserById((Integer)map.get("fromUser")));
            messageVO.put("entityType", (Integer)map.get("entityType"));
            messageVO.put("entityId", (Integer)map.get("entityId"));


            model.addAttribute("commentNotice", messageVO);

        }


        message = messageService.lastestMessage(user.getId(), FOLLOW_TOPIC);
        messageVO = new HashMap<>();


        if (message != null) {
            messageVO.put("message",message);
            messageVO.put("unread", messageService.unread(user.getId(), FOLLOW_TOPIC));
            messageVO.put("count", messageService.count(user.getId(), FOLLOW_TOPIC));
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> map = JSONObject.parseObject(content, HashMap.class);
            System.out.println(map);
            messageVO.put("fromUser", userService.findUserById((Integer)map.get("fromUser")));
            messageVO.put("entityType", (Integer)map.get("entityType"));
            messageVO.put("entityId", (Integer)map.get("entityId"));

            model.addAttribute("followNotice", messageVO);

        }


        message = messageService.lastestMessage(user.getId(), LIKE_TOPIC);
        messageVO = new HashMap<>();


        if (message != null) {
            messageVO.put("message",message);
            messageVO.put("unread", messageService.unread(user.getId(), LIKE_TOPIC));
            messageVO.put("count", messageService.count(user.getId(), LIKE_TOPIC));
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> map = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("fromUser", userService.findUserById((Integer)map.get("fromUser")));
            messageVO.put("entityType", (Integer)map.get("entityType"));
            messageVO.put("entityId", (Integer)map.get("entityId"));

            model.addAttribute("likeNotice", messageVO);

        }



        model.addAttribute("totalLetterUnread", messageService.totalUnreadCount(user.getId()));
        model.addAttribute("totalNoticeUnread", messageService.unread(user.getId(), null));

        return "site/notice";

    }

    @GetMapping("/dataPage")
    public String dataPage(){
        return "site/admin/data";
    }



}
