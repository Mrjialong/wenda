package com.wenda.wenda.async.handler;

import com.wenda.wenda.async.EventHandler;
import com.wenda.wenda.async.EventModel;
import com.wenda.wenda.async.EventType;
//import com.wenda.wenda.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.events.Event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LoginExceptionHandler implements EventHandler {

//    @Autowired
//    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        //判断这个用户的登陆异常
        Map<String,Object> map = new HashMap<String, Object>();

    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.login);
    }
}
