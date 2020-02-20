package com.wenda.wenda.interceptor;

import com.wenda.wenda.dao.LoginTicketDao;
import com.wenda.wenda.dao.UserDao;
import com.wenda.wenda.model.HostHolder;
import com.wenda.wenda.model.LoginTicket;
import com.wenda.wenda.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginRequredInterceptor implements HandlerInterceptor {
    @Autowired
    LoginTicketDao loginTicketDao;
    @Autowired

    UserDao userDao;
    @Autowired
    HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = null;
        //判断是否登录
        if (hostHolder.getUsers() == null){
            response.sendRedirect("/reglogin?next="+request.getRequestURI());

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
