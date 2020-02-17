package com.wenda.wenda.controller;

import com.wenda.wenda.aspect.LogAspect;
import com.wenda.wenda.model.Question;
import com.wenda.wenda.model.ViewObject;
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

import java.util.ArrayList;
import java.util.List;


@Controller
public class homeController {
    @Autowired
    UserServive userServive;
    @Autowired
    QuestionServive questionServive;

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @RequestMapping(path = {"/", "/index"},method = {RequestMethod.GET})
    public String index(Model model){
        model.addAttribute("vos",getQuestion(0,0,10));
        return "index";
    }

    @RequestMapping(path = {"/user/{userId}"},method = {RequestMethod.GET})
    public String userIndex(Model model, @PathVariable("userId") int userId){
        model.addAttribute("vos",getQuestion(userId,0,10));
        return "index";

    }
    private List<ViewObject> getQuestion(int userId ,int offset,int limit){
        List<Question> questionList = questionServive.getlatestQuestion(userId,offset,limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question:questionList){
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            vo.set("user",userServive.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

}


