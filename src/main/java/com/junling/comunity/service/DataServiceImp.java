package com.junling.comunity.service;


import com.junling.comunity.utility.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataServiceImp implements DataService{

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");



    @Override
    public void recordUV(String ip) {

        String key = RedisKey.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(key, ip);
        System.out.println("****");
    }

    @Override
    public long calculateUV(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("date cannot be null");
        }

        Calendar calendar = Calendar.getInstance();
        List<String> list = new ArrayList<>();
        calendar.setTime(startDate);

        while(!calendar.getTime().after(endDate)) {
            String key = RedisKey.getUVKey(df.format(calendar.getTime()));
            list.add(key);
            calendar.add(Calendar.DATE, 1);
        }


        String uvKey = RedisKey.getUVKey(df.format(startDate), df.format(endDate));
        redisTemplate.opsForHyperLogLog().union(uvKey, list.toArray());
        return redisTemplate.opsForHyperLogLog().size(uvKey);
    }

    @Override
    public void recordDAU(int userId) {
        String key = RedisKey.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(key, userId, true);

    }

    @Override
    public long calculateDAU(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("date cannot be null");
        }

        List<byte[]> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while(!calendar.getTime().after(endDate)) {
            String key = RedisKey.getDAUKey(df.format(calendar.getTime()));
            list.add(key.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        return (long)redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                String redisKey = RedisKey.getDAUKey(df.format(startDate), df.format(endDate));
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR, redisKey.getBytes(), list.toArray(new byte[0][0]));
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });

    }
}
