package com.junling.comunity.controller;


import com.alibaba.fastjson.JSONObject;
import com.junling.comunity.entity.Event;
import com.junling.comunity.entity.Message;
import com.junling.comunity.entity.User;
import com.junling.comunity.service.MessageService;
import com.junling.comunity.service.UserService;
import com.junling.comunity.utility.Constant;
import com.junling.comunity.utility.HostHolder;
import com.junling.comunity.utility.JsonUtil;
import com.junling.comunity.utility.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/message")
public class MessageController implements Constant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @GetMapping("/letterDetail/{conversationId}")
    public String letterDetail(@PathVariable("conversationId") String conversationId, Model model, PageUtil page){
        page.setPath("/letterDetail/" + conversationId);
        page.setRowsPerPage(5);
        page.setTotalRows(messageService.totalMessagesPerConversation(conversationId));

        List<Message> messages = messageService.messagesPerConversation(conversationId, page.getOffset(), page.getRowsPerPage());
        List<Map<String, Object>> messageVOList = new ArrayList<>();
        for (Message message: messages) {
            Map<String, Object> messageVO = new HashMap<>();
            int fromId = message.getFromId();
            int toId = message.getToId();
            messageVO.put("fromUser", userService.findUserById(fromId));
            messageVO.put("toUser", userService.findUserById(toId));
            messageVO.put("letter", message);
            messageVOList.add(messageVO);
        }
        model.addAttribute("messages", messageVOList);
        model.addAttribute("page", page);

        User user = hostHolder.getUser();
        int id1 = Integer.parseInt(conversationId.split("_")[0]);
        int id2 = Integer.parseInt(conversationId.split("_")[1]);

        int targetId = user.getId()==id1?id2:id1;
        model.addAttribute("target", userService.findUserById(targetId));

        messageService.updateStatus(conversationId, user.getId(), 1);

        return "site/letter-detail";

    }

    @PostMapping("/publish")
    @ResponseBody
    public String publish(String toUsername, String content) {
        User user = hostHolder.getUser();
        User target = userService.findUserByUsername(toUsername);
        if (target == null) {
            return JsonUtil.getJsonString(403, "username is not valid");
        }
        int fromId = user.getId();
        int toId = target.getId();

        String conversationId = (fromId < toId) ? (fromId + "_" + toId) : (toId + "_" + fromId);

        Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setContent(content);
        message.setConversationId(conversationId);
        message.setCreateTime(new Date());
        message.setStatus(0);
        messageService.insertMessage(message);





        return JsonUtil.getJsonString(0, "update is successful");
    }

    @GetMapping("/noticeDetail/{topic}")
    public String noticeDetail(@PathVariable("topic") String topic, Model model, PageUtil page){
        User user = hostHolder.getUser();
        page.setPath("/noticeDetail/" + topic);
        page.setRowsPerPage(5);

        noticeHelper(page, user, model, topic);
        model.addAttribute("totalLetterUnread", messageService.totalUnreadCount(user.getId()));
        model.addAttribute("totalNoticeUnreadCount", messageService.unread(user.getId(), null));

        return "site/notice-detail";
    }

    private void noticeHelper(PageUtil page, User user, Model model, String topic) {

        System.out.println("topic " + topic);
        page.setTotalRows(messageService.count(user.getId(),topic));
        List<Message> messages = messageService.noticeList(user.getId(), topic, page.getOffset(), page.getRowsPerPage());
        List<Map<String, Object>> noticeList = new ArrayList<>();
        for (Message message: messages) {
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> data = JSONObject.parseObject(message.getContent(), HashMap.class);
            if (message.getStatus() ==0) {
                messageService.updateStatus(topic, user.getId(), 1);
            }

            map.put("message", message);
            map.put("fromUser", userService.findUserById((Integer)data.get("fromUser")));
            map.put("entityType", (Integer)data.get("entityType"));
            map.put("entityId", (Integer)data.get("entityId"));
            noticeList.add(map);
        }
        model.addAttribute("notices", noticeList);
        model.addAttribute("page", page);

    }
}
