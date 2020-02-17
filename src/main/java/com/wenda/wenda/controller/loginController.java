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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
public class loginController {
    private static final Logger logger = LoggerFactory.getLogger(loginController.class);

    @Autowired
    UserServive userServive;


    @RequestMapping(path = {"/reg/"},method = {RequestMethod.POST})
    public String reg(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password
        ){
        try {
            Map<String,String> map = userServive.register(username,password);
            if (map.containsKey("msg")){
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
            return "redirect:/";
        }catch (Exception e){
            logger.error("注册异常");
            return "login";
        }
    }
    @RequestMapping(path = {"/reglogin"},method = {RequestMethod.GET})
    public String reg(Model model){
        return "login";
    }

    @RequestMapping(path = {"/reg/"},method = {RequestMethod.POST})
    public String login(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "rememberme",defaultValue = "false")boolean rememberme
    ){
        try {
            Map<String,String> map = userServive.login(username,password);
            if (map.containsKey("msg")){
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
            return "redirect:/";
        }catch (Exception e){
            logger.error("注册异常");
            return "login";
        }
    }




}


