package com.wenda.wenda.async;

import com.alibaba.fastjson.JSON;

import com.wenda.wenda.async.EventHandler;
import com.wenda.wenda.async.EventType;
import com.wenda.wenda.util.JedisAdapter;
import com.wenda.wenda.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import sun.java2d.pipe.hw.AccelDeviceEventNotifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by nowcoder on 2016/7/30.
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    //config是一个map，Map<EventType, List<EventHandler>>存贮了EvenetType事件类型，List<EventHandler>跟事件绑定的handler
    private Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        //找到继承Eventhandler的所有的类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            //遍历所有的handler
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                //eventTypes为handler所要关联的event
                List<EventType> eventTypes = entry.getValue().getSupportEventType();

                for (EventType type : eventTypes) {
                    //如果config中没有这种类型，加入这种类型
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    //有这种类型，直接加入
                    config.get(type).add(entry.getValue());
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    String key = RedisKeyUtil.getEventqueueKey();
                    //获取事件
                    List<String> events = jedisAdapter.brpop(0, key);


                    for (String message : events) {
                        //RedisKeyUtil的getEventqueueKey会在前面加入一个key，要过滤掉
                        if (message.equals(key)) {
                            continue;
                        }
                        //反序列化为EventModel
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别的事件");
                            continue;
                        }
                        //从config中获取队列中的event类型所需要的handler
                        for (EventHandler handler : config.get(eventModel.getType())) {
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
