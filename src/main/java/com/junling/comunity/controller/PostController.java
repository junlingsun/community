package com.junling.comunity.controller;


import com.junling.comunity.entity.Comment;
import com.junling.comunity.entity.Event;
import com.junling.comunity.entity.Post;
import com.junling.comunity.entity.User;
import com.junling.comunity.kafka.EventProducer;
import com.junling.comunity.service.CommentService;
import com.junling.comunity.service.LikeService;
import com.junling.comunity.service.PostService;

import com.junling.comunity.service.UserService;
import com.junling.comunity.utility.Constant;
import com.junling.comunity.utility.HostHolder;
import com.junling.comunity.utility.JsonUtil;
import com.junling.comunity.utility.PageUtil;
import com.zaxxer.hikari.util.IsolationLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/post")
public class PostController implements Constant {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;


    @PostMapping("/publish")
    @ResponseBody
    public String publish(Post post) {
        System.out.println("publish");

        return postService.insertPost(post);
    }

    @GetMapping("/detail/{postId}")
    public String postDetail(@PathVariable("postId") int postId, Model model, PageUtil page){
        Post post = postService.findPostById(postId);
        int userId = post.getUserId();
        User user = userService.findUserById(userId);
        model.addAttribute("post", post);
        model.addAttribute("user", user);

        page.setRowsPerPage(5);
        page.setTotalRows(post.getCommentCount());
        page.setPath("/post/detail/" + postId);

        List<Comment> comments = commentService.comments(POST_TYPE, post.getId(), page.getOffset(), page.getRowsPerPage());
        List<Map<String, Object>> commentVOList = new ArrayList<>();

        for (Comment comment: comments) {
            Map<String, Object> commentVO = new HashMap<>();
            commentVO.put("comment", comment);
            commentVO.put("user", userService.findUserById(comment.getUserId()));
            long likeCount = likeService.likeCount(COMMENT_TYPE, comment.getId());
            int likeStatus = hostHolder.getUser()==null?0:likeService.likeStatus(hostHolder.getUser().getId(), COMMENT_TYPE, comment.getId());
            commentVO.put("commentLikeCount", likeCount);
            commentVO.put("commentLikeStatus", likeStatus);

            List<Comment> replies = commentService.comments(COMMENT_TYPE, comment.getId(), 0, Integer.MAX_VALUE);
            List<Map<String, Object>> replyVOLit = new ArrayList<>();
            for (Comment reply: replies) {
                Map<String, Object> replyVO = new HashMap<>();
                replyVO.put("reply", reply);
                replyVO.put("user", userService.findUserById(reply.getUserId()));
                User target = reply.getTargetId() == 0?null:userService.findUserById(reply.getTargetId());
                replyVO.put("target", target);
                likeCount = likeService.likeCount(COMMENT_TYPE, reply.getId());
                likeStatus = hostHolder.getUser()==null?0:likeService.likeStatus(hostHolder.getUser().getId(), COMMENT_TYPE, reply.getId());
                replyVO.put("replyLikeCount", likeCount);
                replyVO.put("replyLikeStatus", likeStatus);
                replyVOLit.add(replyVO);
            }
            commentVO.put("replies", replyVOLit);
            commentVO.put("replyCount", replyVOLit.size());
            commentVOList.add(commentVO);

        }

        model.addAttribute("comments", commentVOList);

        //like update
        long likeCount = likeService.likeCount(POST_TYPE, postId);
        int likeStatus = hostHolder.getUser()==null?0:likeService.likeStatus(hostHolder.getUser().getId(), POST_TYPE, postId);
        model.addAttribute("postLikeCount", likeCount);
        model.addAttribute("postLikeStatus", likeStatus);


        return "site/discuss-detail";
    }

    @PostMapping("/comment/{postId}")
    public String comment(@PathVariable("postId") int postId, Comment comment){
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.insertComment(comment);

        Event event = new Event();
        event.setTopic(MESSAGE_TOPIC)
                .setUserId(user.getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setMap("postId", postId);
//        eventProducer.fireEvent(event);

        if (comment.getEntityType() == POST_TYPE) {
            event.setEntityUserId(postService.findPostById(postId).getUserId());
        }else {
            event.setEntityUserId(commentService.findCommentById(postId).getUserId());
        }

        eventProducer.fireEvent(event);
        return "redirect:/post/detail/" + postId;
    }

    @GetMapping("/top/{postId}")
    public String top(@PathVariable("postId") int postId) {

        Post post = postService.findPostById(postId);

        postService.updateType(postId, 1);
        Event event = new Event();
        event.setTopic(POST_TOPIC);
        event.setEntityType(POST_TYPE);
        event.setEntityId(postId);
        event.setUserId(hostHolder.getUser().getId());
        event.setEntityUserId(post.getUserId());
        eventProducer.fireEvent(event);

        return"redirect:/post/detail/"+postId ;
    }

    @GetMapping("/wonderful/{postId}")
    public String wonderful(@PathVariable("postId") int postId) {
        Post post = postService.findPostById(postId);

        postService.updateStatus(postId, 1);
        Event event = new Event();
        event.setTopic(POST_TOPIC);
        event.setEntityType(POST_TYPE);
        event.setEntityId(postId);
        event.setUserId(hostHolder.getUser().getId());
        event.setEntityUserId(post.getUserId());
        eventProducer.fireEvent(event);


        return"redirect:/post/detail/"+postId ;
    }

    @GetMapping("/delete/{postId}")
    public String delete(@PathVariable("postId") int postId) {
        Post post = postService.findPostById(postId);

        postService.updateStatus(postId, 2);
        Event event = new Event();
        event.setTopic(DELETE_TOPIC);
        eventProducer.fireEvent(event);


        return"redirect:/index";
    }


}
