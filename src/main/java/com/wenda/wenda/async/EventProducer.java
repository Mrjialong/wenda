package com.wenda.wenda.async;

import com.alibaba.fastjson.JSONObject;
import com.wenda.wenda.util.JedisAdapter;
import com.wenda.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.ref.SoftReference;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 把事件推到队列里面去
     * @param eventModel 事件
     * @return
     */
    public boolean fireEvent(EventModel eventModel){
        try{
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventqueueKey();
            jedisAdapter.lpush(key,json);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
