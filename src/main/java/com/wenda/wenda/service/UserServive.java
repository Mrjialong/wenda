package com.wenda.wenda.service;

import com.wenda.wenda.dao.LoginTicketDao;
import com.wenda.wenda.dao.UserDao;
import com.wenda.wenda.model.LoginTicket;
import com.wenda.wenda.model.User;


import com.wenda.wenda.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class UserServive {
    @Autowired
    LoginTicketDao loginTicketDao;
    @Autowired
    UserDao userDao;

    /**
     * 获取用户
     * @param userId
     * @return
     */
    public User getUser(int userId){
        return userDao.selectById(userId);
    }

    /**
     * 注册用户
     * @param username
     * @param password
     * @return
     */
    public Map<String,String> register(String username,String password){
        Map<String,String> map = new HashMap<String,String>();
        if (StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        User user = userDao.selectByName(username);
        if (user != null){
            map.put("msg","用户名已经被注册");
            return map;
        }
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        userDao.addUser(user);
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;

        }

    /**
     * 用户登陆
     * @param username
     * @param password
     * @return
     */
    public Map<String,String> login(String username,String password){
        Map<String,String> map = new HashMap<String,String>();
        if (StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        User user = userDao.selectByName(username);
        if (user == null){
            map.put("msg","用户名不存在");
            return map;
        }
        if (!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码错误");
            return map;
        }
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;

    }

    /**
     * 添加ticket
     * @param userId
     * @return
     */
    public String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date now = new Date();
        now.setTime(3600*24*100+now.getTime());
        loginTicket.setExpired(now);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDao.addTicket(loginTicket);
        return loginTicket.getTicket();

    }

}
