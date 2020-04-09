package com.wenda.wenda.service;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.wenda.wenda.util.JedisAdapter;
import com.wenda.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
@Component
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 关注功能
     * @param userId 执行关注操作的用户名
     * @param entityType 关注的类型
     * @param entityId 关注的Id
     * @return
     */
    public boolean follow(int userId,int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFolowerKey(entityType,entityId);
        String followeeKey = RedisKeyUtil.getFoloweeKey(userId,entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        //关注对象的粉丝列表把执行关注操作的用户加上
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));
        //执行关注操作的用户的关注列表把关注对象加上
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx,jedis);
        return ret.size() == 2 && (long) ret.get(0)>0 && (long)ret.get(1)>0;
    }
    /**
     * 取消关注功能
     * @param userId 执行取消关注操作的用户名
     * @param entityType 取消关注的类型
     * @param entityId 取消关注的Id
     * @return
     */
    public boolean unfollow(int userId,int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFolowerKey(entityType,entityId);
        String followeeKey = RedisKeyUtil.getFoloweeKey(userId,entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        //关注对象的粉丝列表把执行关注操作的用户删除
        tx.zrem(followerKey,String.valueOf(userId));
        //执行关注操作的用户的关注列表把关注对象删除
        tx.zrem(followeeKey,String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx,jedis);
        return ret.size() == 2 && (long) ret.get(0)>0 && (long)ret.get(1)>0;
    }
    private List<Integer> getIdsFormSet(Set<String> idset){
        List<Integer> ids = new ArrayList<Integer>();
        for (String str : idset){
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }
    //获取粉丝
    public List<Integer> getFollowers(int entitytype,int entityId,int count){
        String followerKey = RedisKeyUtil.getFolowerKey(entitytype,entityId);
        return getIdsFormSet(jedisAdapter.zrevrange(followerKey,0,count));
    }

    public List<Integer> getFollowers(int entitytype,int entityId, int offset,int count){
        String followerKey = RedisKeyUtil.getFolowerKey(entitytype,entityId);
        return getIdsFormSet(jedisAdapter.zrevrange(followerKey,offset,count));
    }
    //获取关注者
    public List<Integer> getFollowees(int entitytype,int entityId,int count){
        String followeeKey = RedisKeyUtil.getFoloweeKey(entitytype,entityId);
        return getIdsFormSet(jedisAdapter.zrevrange(followeeKey,0,count));
    }

    public List<Integer> getFollowees(int entitytype,int entityId, int offset,int count){
        String followeeKey = RedisKeyUtil.getFoloweeKey(entitytype,entityId);
        return getIdsFormSet(jedisAdapter.zrevrange(followeeKey,offset,count));
    }
    //关注总数
    public long getFolloweeCount(int entityType,int entityId){
        String followeeKey = RedisKeyUtil.getFoloweeKey(entityType,entityId);
        return jedisAdapter.zcard(followeeKey);
    }
    //粉丝总数
    public long getFollowerCount(int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFolowerKey(entityType,entityId);
        return jedisAdapter.zcard(followerKey);
    }
    //判断是否在粉丝列表
    public boolean isFollower(int userId,int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFolowerKey(entityType,entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId)) != null;
    }

}
