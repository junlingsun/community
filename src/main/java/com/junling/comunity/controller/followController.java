package com.junling.comunity.controller;


import com.junling.comunity.entity.Event;
import com.junling.comunity.entity.User;
import com.junling.comunity.kafka.EventProducer;
import com.junling.comunity.service.FollowService;
import com.junling.comunity.service.UserService;
import com.junling.comunity.utility.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class followController implements Constant {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int targetId, int activeId) {

        Map<String, Object> map = new HashMap<>();
        boolean isFollowed = followService.hasFollowed(targetId, activeId);


        if (isFollowed) {
            followService.unfollow(targetId,activeId);
        }else {
            followService.follow(targetId, activeId);
            Event event = new Event();
            event.setEntityId(targetId)
                    .setEntityType(USER_TYPE)
                    .setTopic(FOLLOW_TOPIC)
                    .setUserId(activeId)
                    .setEntityUserId(targetId);

            eventProducer.fireEvent(event);
        }


        map.put("isFollowed", followService.hasFollowed(targetId, activeId));
        map.put("followerCount", followService.followerCount(targetId));
        map.put("followeeCount", followService.followeeCount(targetId));


        return JsonUtil.getJsonString(0, "follow successfully", map);
    }

    @GetMapping("/followee/{userId}")
    public String followee(@PathVariable("userId") int userId, Model model, PageUtil page){
        User targetUser = userService.findUserById(userId);
        model.addAttribute("targetUser", targetUser);
        String followeeKey = RedisKey.getFolloweeKey(userId);
        page.setTotalRows((int)followService.followeeCount(userId));
        page.setRowsPerPage(5);
        page.setPath("/followee/"+userId);
        List<Map<String, Object>> list = followService.followees(userId, page.getOffset(), page.getRowsPerPage());
        model.addAttribute("page", page);
        model.addAttribute("followees", list);

        return "site/followee";

    }

    @GetMapping("/follower/{userId}")
    public String follower(@PathVariable("userId") int userId, Model model, PageUtil page){
        User targetUser = userService.findUserById(userId);
        model.addAttribute("targetUser", targetUser);
        String followerKey = RedisKey.getFollowerKey(userId);
        page.setTotalRows((int)followService.followerCount(userId));
        page.setRowsPerPage(5);
        page.setPath("/follower/"+userId);
        List<Map<String, Object>> list = followService.followers(userId, page.getOffset(), page.getRowsPerPage());
        model.addAttribute("page", page);
        model.addAttribute("followers", list);

        return "site/follower";

    }
}
