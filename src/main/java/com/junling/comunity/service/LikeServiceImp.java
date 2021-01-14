package com.junling.comunity.service;

import com.junling.comunity.utility.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class LikeServiceImp implements LikeService{

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public long likeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKey.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    @Override
    public void like(int userId, int entityType, int entityId, int toUserId) {


        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKey.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKey.getUserLikeKey(toUserId);
                boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);

                redisOperations.multi();
                if (isMember) {
                    redisTemplate.opsForSet().remove(entityLikeKey, userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                }else {
                    redisTemplate.opsForSet().add(entityLikeKey, userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }
                return redisOperations.exec();
            }
        });




    }

    @Override
    //0-not like, 1- like
    public int likeStatus(int userId, int entityType, int entityId) {
        String key = RedisKey.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(key, userId) ? 1: 0;
    }

    @Override
    public int userLikeCount(int userId) {
        String key = RedisKey.getUserLikeKey(userId);
        return redisTemplate.opsForValue().get(key)==null?0:(int)redisTemplate.opsForValue().get(key);
    }
}
