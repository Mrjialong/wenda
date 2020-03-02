package com.wenda.wenda.service;

import com.wenda.wenda.model.HostHolder;
import com.wenda.wenda.util.JedisAdapter;
import com.wenda.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;
    @Autowired
    HostHolder hostHolder;

    public long getLikeCount(int entityType,int entityid){
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityid);
        return jedisAdapter.scard(likeKey);

    }

    public int getLikeStatus(int userID,int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        if (jedisAdapter.sismember(likeKey,String.valueOf(userID))){
            return 1;
        }
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityType,entityId);
        return jedisAdapter.sismember(dislikeKey,String.valueOf(userID))?-1:0;

    }

    public long like(int userId,int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.sadd(likeKey,String.valueOf(userId));
        String disLikeKey = RedisKeyUtil.getDislikeKey(entityType,entityId);
        jedisAdapter.srem(disLikeKey,String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }
    public long dislike(int userId,int entityType,int entityId){
        String disLikeKey = RedisKeyUtil.getDislikeKey(entityType,entityId);
        jedisAdapter.sadd(disLikeKey,String.valueOf(userId));
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.srem(likeKey,String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }
}
