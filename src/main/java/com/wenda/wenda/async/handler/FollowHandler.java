package com.wenda.wenda.async.handler;

import com.wenda.wenda.async.EventHandler;
import com.wenda.wenda.async.EventModel;
import com.wenda.wenda.async.EventType;
import com.wenda.wenda.model.EntityType;
import com.wenda.wenda.model.Message;
import com.wenda.wenda.model.User;
import com.wenda.wenda.service.MessageService;
import com.wenda.wenda.service.UserServive;
import com.wenda.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class FollowHandler implements EventHandler {
    @Autowired
    MessageService messageService;
    @Autowired
    UserServive userServive;
    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userServive.getUser(model.getActorId());
        if (model.getEntityType() == EntityType.ENTITY_QUESTION){
            message.setContent("用户"+user.getName()+"关注了你的问题,http://127.0.0.1:8080/question/"+model.getEntityId());
        }else if (model.getEntityType() == EntityType.ENTITY_USER){
            message.setContent("用户"+user.getName()+"关注了你,http://127.0.0.1:8080/user/"+model.getActorId());
        }
        System.out.println("followhandler");

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
