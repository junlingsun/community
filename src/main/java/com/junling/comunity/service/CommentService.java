package com.junling.comunity.service;

import com.junling.comunity.entity.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> comments(int entityType, int entityId, int offset, int limit);

    int insertComment(Comment comment);

    Comment findCommentById(int id);
}
