package com.wenda.wenda.service;

import com.wenda.wenda.dao.MessageDao;
import com.wenda.wenda.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageDao messageDao;
    @Autowired
    SensitiveService sensitiveService;

    /**
     * 添加消息
     * @param message
     * @return
     */
    public int addMessage(Message message){
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDao.addMessage(message) > 0? message.getId():0;
    }
    public List<Message> getConversationDetail(String conversationId ,int offset,int limit){
        return messageDao.getConversationDetail(conversationId,offset,limit);
    }

    public List<Message> getConversationList(int userId ,int offset,int limit){
        return messageDao.getConversationList(userId,offset,limit);
    }
   public int getConvesationUnreadCount(int userId,String conversationId){
        return messageDao.getConvesationUnreadCount(userId,conversationId);
   }

}
