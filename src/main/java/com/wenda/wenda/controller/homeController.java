package com.wenda.wenda.controller;

import com.wenda.wenda.aspect.LogAspect;
import com.wenda.wenda.model.*;
import com.wenda.wenda.service.CommentService;
import com.wenda.wenda.service.FollowService;
import com.wenda.wenda.service.QuestionServive;
import com.wenda.wenda.service.UserServive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.List;


@Controller
public class homeController {
    @Autowired
    UserServive userServive;
    @Autowired
    QuestionServive questionServive;
    @Autowired
    FollowService followService;
    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);



    /**
     * 问题首页
     * @param model
     * @return index页面
     */
    @RequestMapping(path = {"/", "/index"},method = {RequestMethod.GET})
    public String index(Model model,
                        @RequestParam(value = "pop", defaultValue = "0") int pop){
        model.addAttribute("vos",getQuestion(0,0,10));
        return "index";
    }

    /**
     * 根据userId获取用户问题页
     * @param model
     * @param userId
     * @return index页面
     */
    @RequestMapping(path = {"/user/{userId}"},method = {RequestMethod.GET,RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId){
        model.addAttribute("vos",getQuestion(userId,0,10));
        User user = userServive.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user",user);
        vo.set("commentCount",commentService.getCommentCount(EntityType.ENTITY_USER,userId));
        vo.set("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,userId));
        vo.set("followeeCount",followService.getFolloweeCount(userId,EntityType.ENTITY_USER));
        if (hostHolder.getUsers() != null){
            vo.set("followed",followService.isFollower(hostHolder.getUsers().getId(),EntityType.ENTITY_USER,userId));
        }else {
            vo.set("followed",false);
        }
        model.addAttribute("profileUser",vo);
        return "profile";

    }

    /**
     * 获取问题序列
     * @param userId 用户名
     * @param offset 起始位置
     * @param limit 限制数量
     * @return 返回List<ViewObject> vos
     */
    private List<ViewObject> getQuestion(int userId ,int offset,int limit){
        List<Question> questionList = questionServive.getlatestQuestion(userId,offset,limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question:questionList){
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            vo.set("followCount",followService.getFollowerCount(EntityType.ENTITY_QUESTION,question.getId()));
            vo.set("user",userServive.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

}


