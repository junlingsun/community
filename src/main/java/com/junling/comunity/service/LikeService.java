package com.junling.comunity.service;

import java.util.Set;

public interface LikeService {
    long likeCount(int entityType, int entityId);
    void like(int userId, int entityType, int entityId, int toUserId);
    int likeStatus(int userId, int entityType, int entityId);
    int userLikeCount(int userId);

}
