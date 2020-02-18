package com.wenda.wenda.controller;

import com.wenda.wenda.dao.QuestionDao;
import com.wenda.wenda.dao.UserDao;
import com.wenda.wenda.model.Question;
import com.wenda.wenda.model.User;
import com.wenda.wenda.service.WendaServive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class SettingController {
    @Autowired
    WendaServive wendaservice;
    @Autowired
    UserDao userDao;
    @Autowired
    QuestionDao questionDao;
    @RequestMapping(path = {"/setting"},method = {RequestMethod.GET})
    @ResponseBody
    public String index(){
        return "setting OK "+wendaservice.getMessage(1);
    }

    /**
     * 添加用户
     * @return
     */
    @RequestMapping(path = {"/addUser"},method = {RequestMethod.GET})
    @ResponseBody
    public String add(){
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setName(String.format("user%d",i));
            user.setPassword("");
            user.setSalt("111");
            user.setHeadUrl("q111");
            userDao.addUser(user);

        }
        return "setting OK";
    }

    /**
     * 添加问题
     * @return
     */
    @RequestMapping(path = {"/addQuestion"},method = {RequestMethod.GET})
    @ResponseBody
    public String addQuestion(){
        for (int i = 0; i < 10; i++) {
            Question question = new Question();
            question.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime()+1000*3600*i);
            question.setCreatedDate(date);
            question.setUserId(i+1);
            question.setTitle(String.format("title{%d}",i));
            question.setContent(String.format("lalalalalalalalal %d",i));
            questionDao.addQuestion(question);

        }
        return "setting OK";
    }
}
