package com.wenda.wenda.service;

import com.wenda.wenda.dao.UserDao;
import com.wenda.wenda.model.User;


import com.wenda.wenda.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServive {
    @Autowired
    UserDao userDao;
    public User getUser(int userId){
        return userDao.selectById(userId);
    }
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
        return map;

        }
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
        return map;

    }

}
