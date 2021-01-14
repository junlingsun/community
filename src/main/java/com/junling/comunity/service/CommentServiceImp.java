package com.junling.comunity.service;

import com.junling.comunity.dao.CommentDao;
import com.junling.comunity.dao.PostDao;
import com.junling.comunity.entity.Comment;
import com.junling.comunity.entity.Post;
import com.junling.comunity.utility.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImp implements CommentService, Constant {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private PostDao postDao;

    @Override
    public List<Comment> comments(int entityType, int entityId, int offset, int limit) {
        return commentDao.comments(entityType, entityId, offset, limit);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public int insertComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("comment cannot be empty");
        }

        int rows = commentDao.insertComment(comment);
        if(comment.getEntityType() == POST_TYPE) {
            int count = commentDao.commentCount(comment.getEntityType(), comment.getEntityId());
            postDao.updateCommentCount(comment.getEntityId(), count);
        }
//
        return rows;
    }

    @Override
    public Comment findCommentById(int id) {
        return commentDao.findCommentById(id);
    }
}
