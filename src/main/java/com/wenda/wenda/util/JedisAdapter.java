package com.wenda.wenda.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.alibaba.fastjson.JSONPObject;
import com.wenda.wenda.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;

    public static void print(int index, Object obj) {
        System.out.println(String.format("%d, %s", index, obj.toString()));
    }

    public static void main(String[] argv) {
        Jedis jedis = new Jedis("redis://localhost:6379/9");
        jedis.flushDB();

        // get set
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");
        print(1, jedis.get("newhello"));
        jedis.setex("hello2", 1800, "world");

        //
        jedis.set("pv", "100");
        jedis.incr("pv");
        jedis.incrBy("pv", 5);
        print(2, jedis.get("pv"));
        jedis.decrBy("pv", 2);
        print(2, jedis.get("pv"));

        print(3, jedis.keys("*"));
        //list 适合放列表
        String listName = "list";
        jedis.del(listName);
        for (int i = 0; i < 10; ++i) {
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        //取出元素
        //lrange去除区间内的元素
        print(4, jedis.lrange(listName, 0, 12));
        print(4, jedis.lrange(listName, 0, 3));
        //llen查看长度
        print(5, jedis.llen(listName));
        //lpop取出最后插入的一个元素 类似栈结构
        print(6, jedis.lpop(listName));
        print(7, jedis.llen(listName));
        print(8, jedis.lrange(listName, 2, 6));
        //查找指定下标的元素
        print(9, jedis.lindex(listName, 3));
        //插入元素
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb"));
        print(11, jedis.lrange(listName, 0 ,12));

        // hash 适合放不定向的属性值
        String userKey = "userxx";
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey,"phoneNumber","123121212");
        jedis.hset(userKey, "phone", "18618181818");
        print(12, jedis.hget(userKey, "name"));
        print(13, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "phone");
        print(14, jedis.hgetAll(userKey));
        print(15, jedis.hexists(userKey, "email"));
        print(16, jedis.hexists(userKey, "age"));
        print(17, jedis.hkeys(userKey));
        print(18, jedis.hvals(userKey));
        //插入元素如果有则不更改
        jedis.hsetnx(userKey, "school", "zju");
        jedis.hsetnx(userKey, "name", "yxy");
        print(19, jedis.hgetAll(userKey));

        // set 适合去重
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for (int i = 0; i < 10; ++i) {
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(i*i));
        }
        //集合成员
        print(20, jedis.smembers(likeKey1));
        print(21, jedis.smembers(likeKey2));
        //两个集合求并集
        print(22, jedis.sunion(likeKey1, likeKey2));
        //集合likekey1中有集合2likekey2没有的
        print(23, jedis.sdiff(likeKey1, likeKey2));
        //两个集合交集
        print(24, jedis.sinter(likeKey1, likeKey2));
        //查询集合中是否有指定member的元素
        print(25, jedis.sismember(likeKey1, "12"));
        print(26, jedis.sismember(likeKey2, "16"));
        //删除元素
        jedis.srem(likeKey1, "5");
        print(27, jedis.smembers(likeKey1));
        //从前面集合likekey2中把25移动到后面集合likeKey2
        jedis.smove(likeKey2, likeKey1, "25");
        print(28, jedis.smembers(likeKey1));
        //查询集合中有多少人
        print(29, jedis.scard(likeKey1));


        //堆 带有权重 适合排序
        String rankKey = "rankKey";
        //添加元素 例如权重15 的jim
        jedis.zadd(rankKey, 15, "jim");
        jedis.zadd(rankKey, 60, "Ben");
        jedis.zadd(rankKey, 90, "Lee");
        jedis.zadd(rankKey, 75, "Lucy");
        jedis.zadd(rankKey, 80, "Mei");
        //打印多少个元素
        print(30, jedis.zcard(rankKey));
        //输出指定范围内的元素个数
        print(31, jedis.zcount(rankKey, 61, 100));
        print(32, jedis.zscore(rankKey, "Lucy"));
        //加法
        jedis.zincrby(rankKey, 2, "Lucy");
        print(33, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Luc");
        print(34, jedis.zscore(rankKey, "Luc"));
        //集合默认是从小到大排序
        print(35, jedis.zrange(rankKey, 0, 100));
        print(36, jedis.zrange(rankKey, 0, 10));
        print(36, jedis.zrange(rankKey, 1, 3));
        //集合从大到小排序
        print(36, jedis.zrevrange(rankKey, 1, 3));
        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, "60", "100")) {
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }

        //集合里的排名
        print(38, jedis.zrank(rankKey, "Ben"));
        print(39, jedis.zrevrank(rankKey, "Ben"));

        String setKey = "zset";
        jedis.zadd(setKey, 1, "a");
        jedis.zadd(setKey, 1, "b");
        jedis.zadd(setKey, 1, "c");
        jedis.zadd(setKey, 1, "d");
        jedis.zadd(setKey, 1, "e");
        jedis.zadd(setKey, 1,"liming");
        //-为负无穷，+为正无穷,(为不包含，[为包含
        print(40, jedis.zlexcount(setKey, "-", "+"));
        print(41, jedis.zlexcount(setKey, "(b", "[d"));
        print(42, jedis.zlexcount(setKey, "[b", "[d"));
        //删除
        jedis.zrem(setKey, "b");

        print(43, jedis.zrange(setKey, 0, 10));
        //根据字典序排序，c以上的都删除
        jedis.zremrangeByLex(setKey, "(c", "+");
        print(44, jedis.zrange(setKey, 0 ,2));

        /*
        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; ++i) {
            Jedis j = pool.getResource();
            print(45, j.get("pv"));
            j.close();
        }*/

        //连接池 默认8个线程
//        JedisPool pool = new JedisPool();
//        for (int i = 0; i < 100; i++) {
//            Jedis j = pool.getResource();
//            System.out.println(j.get("pv"));
//            j.close();
//        }

        User user2 = new User();
        user2.setName("liming");
        user2.setPassword("121212");
        user2.setSalt("11212");
        jedis.set("user2", JSONObject.toJSONString(user2));
        String value = jedis.get("user2");
        User user3 = JSON.parseObject(value,User.class);
        print(47,user2);
        int k = 2;




//        User user = new User();
//        user.setName("xx");
//        user.setPassword("ppp");
//        user.setHeadUrl("a.png");
//        user.setSalt("salt");
//        user.setId(1);
//        print(46, JSONObject.toJSONString(user));
//        jedis.set("user1", JSONObject.toJSONString(user));
//
//        String value = jedis.get("user1");
//        User user2 = JSON.parseObject(value, User.class);
//        print(47, user2);
//        int k = 2;
    }



    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/10");
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }
}
