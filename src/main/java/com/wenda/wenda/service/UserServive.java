package com.wenda.wenda.service;

import com.wenda.wenda.dao.UserDao;
import com.wenda.wenda.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServive {
    @Autowired
    UserDao userDao;
    public User getUser(int userId){
        return userDao.selectById(userId);
    }
}
