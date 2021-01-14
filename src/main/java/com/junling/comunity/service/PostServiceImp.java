package com.junling.comunity.service;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.junling.comunity.dao.PostDao;
import com.junling.comunity.entity.Event;
import com.junling.comunity.entity.Post;
import com.junling.comunity.entity.User;
import com.junling.comunity.kafka.EventProducer;
import com.junling.comunity.utility.Constant;
import com.junling.comunity.utility.HostHolder;
import com.junling.comunity.utility.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PostServiceImp implements PostService, Constant {

    @Autowired
    private PostDao postDao;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Override
    public List<Post> posts(int userId, int limit, int offset) {
        return postDao.posts(userId, limit, offset);
    }

    @Override
    public int getTotalRows(int userId) {
        return postDao.getTotalRows(userId);
    }

    @Override
    public String insertPost(Post post) {
        if (post == null) {
            int code = 403;
            String msg = "post is empty";
            return JsonUtil.getJsonString(code, msg);
        }
        User user = hostHolder.getUser();
        post.setCommentCount(0);
        post.setCreateTime(new Date());
        post.setScore(0);
        post.setType(0);
        post.setStatus(0);
        post.setUserId(user.getId());


        postDao.insertPost(post);

        Event event = new Event();
        event.setEntityId(post.getId());
        event.setEntityType(POST_TYPE);
        event.setTopic(POST_TOPIC);
        event.setUserId(hostHolder.getUser().getId());
        eventProducer.fireEvent(event);



        return JsonUtil.getJsonString(0, "post is published");
    }

    @Override
    public Post findPostById(int id) {
        return postDao.findPostById(id);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        return postDao.updateCommentCount(id, commentCount);
    }

    @Override
    public void updateType(int id, int type) {
        postDao.updateType(id, type);
    }

    @Override
    public void updateStatus(int id, int status) {
        postDao.updateStatus(id, status);
    }

}
