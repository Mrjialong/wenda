package com.wenda.wenda.controller;

import com.wenda.wenda.model.HostHolder;
import com.wenda.wenda.model.Question;
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
import java.util.Date;
import java.util.Map;


@Controller
public class QuestionController {
    @Autowired
    QuestionServive questionServive;
    @Autowired
    HostHolder hostHolder;
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




}


