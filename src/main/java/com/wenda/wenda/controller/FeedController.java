package com.wenda.wenda.controller;

import com.wenda.wenda.model.EntityType;
import com.wenda.wenda.model.Feed;
import com.wenda.wenda.model.HostHolder;
import com.wenda.wenda.service.FeedService;
import com.wenda.wenda.service.FollowService;
import com.wenda.wenda.util.JedisAdapter;
import com.wenda.wenda.util.RedisKeyUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {
    @Autowired
    FeedService feedService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;


    @RequestMapping(path = {"/pullfeeds"},method = {RequestMethod.GET})
    private String getPullFeeds(Model model){
        int localUserId = hostHolder.getUsers() == null? 0:hostHolder.getUsers().getId();
        List<Integer> followees = new ArrayList<>();
        if (localUserId == 0){
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER,Integer.MAX_VALUE);
        }
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE,followees,10);
        model.addAttribute("feeds",feeds);
        return "feeds";
    }

    @RequestMapping(path = {"/pushfeeds"},method = {RequestMethod.GET})
    private String getPushFeeds(Model model){
        int localUserId = hostHolder.getUsers() == null? 0:hostHolder.getUsers().getId();
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId),0,10);
        List<Feed> feeds = new ArrayList<>();
        for (String feedId :feedIds){
            Feed feed = feedService.getById(Integer.parseInt(feedId));
            if (feed == null){
                continue;
            }
            feeds.add(feed);
        }
        model.addAttribute("feeds",feeds);
        return "feeds";
    }
}
