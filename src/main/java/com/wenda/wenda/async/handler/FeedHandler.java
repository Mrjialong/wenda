package com.wenda.wenda.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.wenda.wenda.async.EventHandler;
import com.wenda.wenda.async.EventModel;
import com.wenda.wenda.async.EventType;
import com.wenda.wenda.dao.FeedDao;
import com.wenda.wenda.model.*;
import com.wenda.wenda.service.*;
import com.wenda.wenda.util.JedisAdapter;
import com.wenda.wenda.util.RedisKeyUtil;
import com.wenda.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FeedHandler implements EventHandler {
    @Autowired
    MessageService messageService;
    @Autowired
    UserServive userServive;
    @Autowired
    QuestionServive questionServive;
    @Autowired
    FeedService feedService;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 获取新鲜事的内容
     * @param model
     * @return
     */
    private String buildFeedData(EventModel model){
        Map<String,String> map =  new HashMap<String,String>();
        //新鲜事的触发者
        User actor = userServive.getUser(model.getActorId());
        if (actor == null){
            return null;
        }
        map.put("userId",String.valueOf(actor.getId()));
        map.put("userhead",actor.getHeadUrl());
        map.put("username",actor.getName());
        //评论或者是关注问题
        if (model.getType() == EventType.COMMENT ||
                (model.getType() == EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_QUESTION)){
            Question question = questionServive.selectById(model.getEntityId());
            if (question == null){
                return null;
            }
            map.put("questionId",String.valueOf(question.getId()));
            map.put("questionTitle",question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;

    }
    public void doHandle(EventModel model) {
        Feed feed = new Feed();
        feed.setCreateDate(new Date());
        feed.setUserId(model.getActorId());
        feed.setType(model.getType().getValue());
        feed.setData(buildFeedData(model));
        if (feed.getData() == null){
            return;
        }
        feedService.addFeed(feed);
        //给事件的粉丝推
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER,model.getActorId(),Integer.MAX_VALUE);
        //系统的队列
        followers.add(0);
        for (int follower:followers){
            String timelinekey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelinekey,String.valueOf(feed.getId()));
        }
    }

    @Override
    public List<EventType> getSupportEventType() {
        //判定了评论和关注
//        return Arrays.asList(EventType.FOLLOW);
        return Arrays.asList(new EventType[]{EventType.COMMENT,EventType.FOLLOW});
    }
}
