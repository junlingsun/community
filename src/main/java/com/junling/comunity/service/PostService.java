package com.junling.comunity.service;

import com.junling.comunity.entity.Post;
import org.springframework.stereotype.Service;

import java.util.List;



public interface PostService {
   List<Post> posts (int userId, int limit, int offset);
   int getTotalRows(int userId);

   String insertPost(Post post);
   Post findPostById(int id);
   int updateCommentCount(int id, int commentCount);
   void updateType(int Id, int type);
   void updateStatus(int id, int status);

}
