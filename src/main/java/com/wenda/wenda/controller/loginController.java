package com.wenda.wenda.controller;

import com.sun.deploy.net.HttpResponse;
import com.wenda.wenda.aspect.LogAspect;
import com.wenda.wenda.async.EventModel;
import com.wenda.wenda.async.EventProducer;
import com.wenda.wenda.async.EventType;
import com.wenda.wenda.model.Question;
import com.wenda.wenda.model.ViewObject;
import com.wenda.wenda.service.QuestionServive;
import com.wenda.wenda.service.UserServive;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
public class loginController {
    private static final Logger logger = LoggerFactory.getLogger(loginController.class);

    @Autowired
    UserServive userServive;
    @Autowired
    EventProducer eventProducer;

    /**
     * 注册功能，生成ticket，并用response返回ticket存储到cookie中
     * @param model
     * @param username 用户名
     * @param password 密码
     * @param httpServletResponse
     * @return 成功返回/，失败返回login
     */
    @RequestMapping(path = {"/reg/"},method = {RequestMethod.POST})
    public String reg(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      HttpServletResponse httpServletResponse,
                      @RequestParam(value = "next",required = false) String next
        ){
        try {
            Map<String,String> map = userServive.register(username,password);
            if (map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");
                httpServletResponse.addCookie(cookie);
                if (StringUtils.isNotBlank(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";

            }else {
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("注册异常");
            return "login";
        }
    }

    /**
     * 跳转登陆页面
     * @param model
     * @return login页面
     */
    @RequestMapping(path = {"/reglogin"},method = {RequestMethod.GET})
    public String reg(Model model,
                      @RequestParam(value = "next",required = false) String next
    ){
        model.addAttribute("next",next);
        return "login";
    }

    /**
     * 登陆按钮功能：验证用户名和密码并生成ticket，并用response返回ticket存储到cookie中
     * @param model
     * @param username 用户名
     * @param password 密码
     * @param rememberme 是否记住用户名
     * @param httpServletResponse 返回ticket
     * @return 成功返回/，失败返回login
     */
    @RequestMapping(path = {"/login/"},method = {RequestMethod.POST})
    public String login(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                        @RequestParam(value = "next",required = false) String next,
                        @RequestParam(value = "rememberme",defaultValue = "false")boolean rememberme,
                        HttpServletResponse httpServletResponse
    ){
        try {
            Map<String,String> map = userServive.login(username,password);
            if (map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");
                httpServletResponse.addCookie(cookie);
                //发动邮件组件
//                eventProducer.fireEvent(new EventModel(EventType.login)
//                        .setExt("username",username)
//                        .setExt("email","1040147144@qq.com")
//                        .setActorId(Integer.parseInt(map.get("userId"))));
                if (StringUtils.isNotBlank(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";

            }else {
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("登陆异常");
            return "login";
        }
    }


    /**
     * 退出登陆
     * @param ticket
     * @return 首页
     */
    @RequestMapping(path = {"/logout"})
    public String logout(@CookieValue("ticket") String ticket){
        userServive.logout(ticket);
        return "redirect:/";
    }




}


