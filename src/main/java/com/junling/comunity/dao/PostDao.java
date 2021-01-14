package com.junling.comunity.dao;


import com.junling.comunity.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PostDao {

    List<Post> posts(int userId, int limit, int offset);
    int getTotalRows(@Param("userId") int userId);
    int insertPost(Post post);
    Post findPostById(int id);
    int updateCommentCount(int id, int commentCount);
    int updateType(int id, int type);
    int updateStatus(int id, int status);


}
