package com.junling.comunity.service;

import java.util.List;
import java.util.Map;

public interface FollowService {

    void follow(int targetId, int activeId);
    void unfollow(int targetId, int activeId);
    boolean hasFollowed(int targetId, int activeId);
    long followerCount(int userId);
    long followeeCount(int userId);
    List<Map<String, Object>> followers(int userId, int offset, int limit);
    List<Map<String, Object>> followees(int userId, int offset, int limit);
}
