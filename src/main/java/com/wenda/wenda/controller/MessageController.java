package com.wenda.wenda.controller;

import com.wenda.wenda.aspect.LogAspect;
import com.wenda.wenda.model.HostHolder;
import com.wenda.wenda.model.Message;
import com.wenda.wenda.model.User;
import com.wenda.wenda.model.ViewObject;
import com.wenda.wenda.service.MessageService;
import com.wenda.wenda.service.UserServive;
import com.wenda.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserServive userServive;
    @Autowired
    MessageService messageService;

    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        try {
            if (hostHolder.getUsers() == null) {
                return WendaUtil.getJSONString(999, "未登录");
            }
            User user = userServive.selectByName(toName);
            if (user == null) {
                return WendaUtil.getJSONString(1, "用户不存在");
            }

            Message msg = new Message();
            msg.setContent(content);
            msg.setFromId(hostHolder.getUsers().getId());
            msg.setToId(user.getId());
            msg.setCreatedDate(new Date());
            //msg.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
            messageService.addMessage(msg);
            return WendaUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("增加站内信失败" + e.getMessage());
            return WendaUtil.getJSONString(1, "插入站内信失败");
        }
    }

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String getConversationList(Model model) {
        if (hostHolder.getUsers() == null){
            return "redirect:/reglogin";
        }
        int localUserId = hostHolder.getUsers().getId();
        List<Message> messageList = messageService.getConversationList(localUserId,0,10);
        List<ViewObject> conversations = new ArrayList<ViewObject>();
        for (Message message :messageList){
            ViewObject vo = new ViewObject();
            vo.set("conversation",message);
//          targetId为对方ID 判断message.getFromId()是否等于localUserId（自身ID）
//          是的则证明对方ID不是message.getFromId()返回message.getToId()
//          否则返回message.getFromId();
            int targetId = message.getFromId() == localUserId? message.getToId():message.getFromId();
            vo.set("user",userServive.getUser(targetId));
            vo.set("unread",messageService.getConvesationUnreadCount(localUserId,message.getConversationId()));
            conversations.add(vo);
        }
        model.addAttribute("conversations",conversations);
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String getConversationDetail(Model model,@RequestParam("conversationId") String conversationId) {
        try{
            List<Message> messageList = messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> messages = new ArrayList<ViewObject>();
            for (Message message : messageList){
                ViewObject vo = new ViewObject();
                vo.set("message",message);
                vo.set("user",userServive.getUser(message.getFromId()));
                messages.add(vo);

            }
            model.addAttribute("messages" ,messages);
        }catch (Exception e){
            logger.error("获取详情失败"+e.getMessage());
        }
        return "letterDetail";
    }
}
