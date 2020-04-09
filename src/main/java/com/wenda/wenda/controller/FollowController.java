package com.wenda.wenda.controller;

import com.wenda.wenda.aspect.LogAspect;
import com.wenda.wenda.async.EventModel;
import com.wenda.wenda.async.EventProducer;
import com.wenda.wenda.async.EventType;
import com.wenda.wenda.model.*;
import com.wenda.wenda.service.CommentService;
import com.wenda.wenda.service.FollowService;
import com.wenda.wenda.service.QuestionServive;
import com.wenda.wenda.service.UserServive;
import com.wenda.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);


    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionServive questionServive;
    @Autowired
    UserServive userServive;
    @Autowired
    FollowService followService;
    @Autowired
    EventProducer eventProducer;

    //关注用户
    @RequestMapping(path = {"/followUser"},method = {RequestMethod.POST})
    @ResponseBody
    public String follow(@RequestParam("userid") int userId){
        if (hostHolder.getUsers() == null){
            return WendaUtil.getJSONString(999);
        }
        boolean ret = followService.follow(hostHolder.getUsers().getId(),EntityType.ENTITY_USER,userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUsers().getId())
                .setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));
        //关注成功为0，否则为1,并返回关注后的关注数量
        return WendaUtil.getJSONString(ret? 0:1,
                String.valueOf(followService.getFolloweeCount(hostHolder.getUsers().getId(),EntityType.ENTITY_USER)));
    }
    //取消关注用户
    @RequestMapping(path = {"/unfollowUser"},method = {RequestMethod.POST})
    @ResponseBody
    public String unfollow(@RequestParam("userid") int userId){
        if (hostHolder.getUsers() == null){
            return WendaUtil.getJSONString(999);
        }
        boolean ret = followService.unfollow(hostHolder.getUsers().getId(),EntityType.ENTITY_USER,userId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUsers().getId())
                .setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));
        return WendaUtil.getJSONString(ret? 0:1,
                String.valueOf(followService.getFolloweeCount(hostHolder.getUsers().getId(),EntityType.ENTITY_USER)));
    }
    //关注问题
    @RequestMapping(path = {"/followQuestion"},method = {RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        if (hostHolder.getUsers() == null){
            return WendaUtil.getJSONString(999);
        }
        Question q = questionServive.selectById(questionId);
        if (q == null){
            return WendaUtil.getJSONString(1,"问题不存在");
        }
        boolean ret = followService.follow(hostHolder.getUsers().getId(),EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUsers().getId())
                .setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));
        Map<String,Object> info = new HashMap<String, Object>();
        info.put("headurl",hostHolder.getUsers().getHeadUrl());
        info.put("name",hostHolder.getUsers().getName());
        info.put("id",hostHolder.getUsers().getId());
        info.put("count",followService.getFolloweeCount(hostHolder.getUsers().getId()
                ,EntityType.ENTITY_QUESTION));
        return WendaUtil.getJSONString(ret? 0:1,info);
    }
    //取消关注问题
    @RequestMapping(path = {"/unfollowQuestion"},method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        if (hostHolder.getUsers() == null){
            return WendaUtil.getJSONString(999);
        }
        Question q = questionServive.selectById(questionId);
        if (q == null){
            return WendaUtil.getJSONString(1,"问题不存在");
        }
        boolean ret = followService.follow(hostHolder.getUsers().getId(),EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUsers().getId())
                .setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(q.getUserId()));
        Map<String,Object> info = new HashMap<String, Object>();
        info.put("headurl",hostHolder.getUsers().getHeadUrl());
        info.put("name",hostHolder.getUsers().getName());
        info.put("id",hostHolder.getUsers().getId());
        info.put("count",followService.getFolloweeCount(hostHolder.getUsers().getId()
                ,EntityType.ENTITY_QUESTION));
        return WendaUtil.getJSONString(ret? 0:1,info);
    }

    //关注列表
    @RequestMapping(path = {"/user/{uid}/followees"},method = {RequestMethod.GET})
    public String followees(Model model,@PathVariable("uid") int userId){
        List<Integer> folllweeIds = followService.getFollowees(userId,EntityType.ENTITY_USER,10);
        if (hostHolder.getUsers() != null){
            model.addAttribute("followees",getUsersInfo(hostHolder.getUsers().getId(),folllweeIds));
        }else {
            model.addAttribute("followees",getUsersInfo(0,folllweeIds));
        }
        return "followees";
    }
    //粉丝列表
    @RequestMapping(path = {"/user/{uid}/followers"},method = {RequestMethod.GET})
    public String followers(Model model,@PathVariable("uid") int userId){
        List<Integer> folllwerIds = followService.getFollowees(userId,EntityType.ENTITY_USER,10);
        if (hostHolder.getUsers() != null){
            model.addAttribute("followers",getUsersInfo(hostHolder.getUsers().getId(),folllwerIds));
        }else {
            model.addAttribute("followers",getUsersInfo(0,folllwerIds));
        }
        return "followers";
    }

    private List<ViewObject> getUsersInfo(int localUserId,List<Integer> userIds){
        List<ViewObject> userInfos = new ArrayList<>();
        for (Integer uid : userIds){
            User user = userServive.getUser(uid);
            if (user == null){
                continue;
            }

            ViewObject vo = new ViewObject();
            vo.set("user",user);
            vo.set("followerCount",followService.getFolloweeCount(EntityType.ENTITY_USER,uid));
            vo.set("followeeCount",followService.getFollowerCount(EntityType.ENTITY_USER,uid));
            if (localUserId != 0){
                vo.set("followed",followService.isFollower(localUserId,EntityType.ENTITY_USER,uid));
            }else {
                vo.set("followed",false);
            }
            userInfos.add(vo);

        }
        return userInfos;
    }
}
