package com.wenda.wenda.service;

import org.springframework.stereotype.Service;

@Service
public class WendaServive {
    public String getMessage(int userId){
        return "Hello Message"+String.valueOf(userId);
    }
}
