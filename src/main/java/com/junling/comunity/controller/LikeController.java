package com.junling.comunity.controller;

import com.junling.comunity.entity.Comment;
import com.junling.comunity.entity.Event;
import com.junling.comunity.entity.Post;
import com.junling.comunity.entity.User;
import com.junling.comunity.kafka.EventProducer;
import com.junling.comunity.service.CommentService;
import com.junling.comunity.service.LikeService;
import com.junling.comunity.service.PostService;
import com.junling.comunity.utility.Constant;
import com.junling.comunity.utility.HostHolder;
import com.junling.comunity.utility.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements Constant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int toUserId, int postId) {
        User user = hostHolder.getUser();

        likeService.like(user.getId(), entityType, entityId, toUserId);
        long likeCount = likeService.likeCount(entityType, entityId);
        int likeStatus = likeService.likeStatus(user.getId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        if (likeStatus == 1) {
            Event event = new Event();
            event.setEntityId(entityId)
                    .setEntityType(entityType)
                    .setEntityUserId(toUserId)
                    .setUserId(user.getId())
                    .setTopic(LIKE_TOPIC)
                    .setMap("postId", postId);
            eventProducer.fireEvent(event);
        }




        return JsonUtil.getJsonString(0, "update complete", map);

    }
}
