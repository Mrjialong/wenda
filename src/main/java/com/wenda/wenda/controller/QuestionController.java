package com.wenda.wenda.controller;

import com.wenda.wenda.model.*;
import com.wenda.wenda.service.CommentService;
import com.wenda.wenda.service.LikeService;
import com.wenda.wenda.service.QuestionServive;
import com.wenda.wenda.service.UserServive;
import com.wenda.wenda.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Controller
public class QuestionController {
    @Autowired
    QuestionServive questionServive;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserServive userServive;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    /**
     * 添加问题
     * @param title 标题
     * @param content 内容
     * @return
     */
    @RequestMapping(path = {"/logout"},method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,@RequestParam("content") String content){
        try{
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            if (hostHolder.getUsers() == null){
                question.setUserId(WendaUtil.ANONYMOUS_USERID);
            }else {
                question.setUserId(hostHolder.getUsers().getId());
            }
            if (questionServive.addQuestion(question)>0){
                return WendaUtil.getJSONString(0);
            }
            questionServive.addQuestion(question);

        }catch (Exception e){
            logger.error("添加问题失败"+e.getMessage());
        }
        return WendaUtil.getJSONString(1,"失败");
    }

    /**
     * 根据问题id显示问题
     * @param model
     * @param qid
     * @return
     */
    @RequestMapping(path = {"/question/{qid}"})
    public String questionDetial(Model model,@PathVariable("qid") int qid){
        Question question = questionServive.selectById(qid);
        model.addAttribute("question",question);
        model.addAttribute("user",userServive.getUser(question.getUserId()));

        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);

        List<ViewObject> comments = new ArrayList<>();

        for (Comment comment : commentList){
            ViewObject vo = new ViewObject();
            vo.set("comment",comment);

            if (hostHolder.getUsers() == null){
                vo.set("liked",0);
            }else {
                vo.set("liked",likeService.getLikeStatus(hostHolder.getUsers().getId(),EntityType.ENTITY_COMMENT,comment.getId()));
            }

            vo.set("likeCount",likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));
            vo.set("user",userServive.getUser(comment.getUserId()));
            comments.add(vo);
        }
        model.addAttribute("comments",comments);
        return "detail";
    }

}


