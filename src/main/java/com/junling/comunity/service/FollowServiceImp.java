package com.junling.comunity.service;


import com.junling.comunity.utility.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowServiceImp implements FollowService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    @Override
    public void follow(int targetId, int activeId) {
        String targetFollowerKey = RedisKey.getFollowerKey(targetId);
        String activeFolloweeKey = RedisKey.getFolloweeKey(activeId);


        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public  Object execute(RedisOperations redisOperations) throws DataAccessException {

                redisOperations.multi();
                long currentTime = System.currentTimeMillis();
                redisTemplate.opsForZSet().add(targetFollowerKey, activeId, currentTime);
                redisTemplate.opsForZSet().add(activeFolloweeKey, targetId, currentTime);
                return redisOperations.exec();
            }
        });
    }

    @Override
    public void unfollow(int targetId, int activeId) {

        String targetFollowerKey = RedisKey.getFollowerKey(targetId);
        String activeFolloweeKey = RedisKey.getFolloweeKey(activeId);


        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public  Object execute(RedisOperations redisOperations) throws DataAccessException {

                redisOperations.multi();
                redisTemplate.opsForZSet().remove(targetFollowerKey, activeId);
                redisTemplate.opsForZSet().remove(activeFolloweeKey, targetId);
                return redisOperations.exec();
            }
        });

    }

    @Override
    public boolean hasFollowed(int targetId, int activeId) {
        String followerKey = RedisKey.getFollowerKey(targetId);
        Double score  = redisTemplate.opsForZSet().score(followerKey, activeId);
        return score == null? false:true;
    }

    @Override
    public long followerCount(int userId) {
        String followerKey = RedisKey.getFollowerKey(userId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    @Override
    public long followeeCount(int userId) {
        String followeeKey = RedisKey.getFolloweeKey(userId);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    @Override
    public List<Map<String, Object>> followers(int userId, int offset, int limit) {
        String key = RedisKey.getFollowerKey(userId);
        Set<Integer> members = redisTemplate.opsForZSet().reverseRange(key, offset, offset+limit-1);
        if (members == null) return null;
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer i: members) {
            Map<String, Object> map = new HashMap<>();
            map.put("user", userService.findUserById(i));
            map.put("followTime", new Date(redisTemplate.opsForZSet().score(key,i).longValue()));
            list.add(map);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> followees(int userId, int offset, int limit) {
        String key = RedisKey.getFolloweeKey(userId);
        Set<Integer> members = redisTemplate.opsForZSet().reverseRange(key, offset, offset+limit-1);
        if (members == null) return null;
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer i: members) {
            Map<String, Object> map = new HashMap<>();
            map.put("user", userService.findUserById(i));
            map.put("followTime", new Date(redisTemplate.opsForZSet().score(key,i).longValue()));
            list.add(map);
        }
        return list;
    }
}
