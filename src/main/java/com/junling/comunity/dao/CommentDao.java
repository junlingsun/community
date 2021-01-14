package com.junling.comunity.dao;

import com.junling.comunity.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CommentDao {

    List<Comment> comments(int entityType, int entityId, int offset, int limit);
    int insertComment(Comment comment);
    int commentCount(int entityType, int entityId);
    Comment findCommentById(int id);
}
