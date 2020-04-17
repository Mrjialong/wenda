package com.wenda.wenda.controller;

import com.wenda.wenda.aspect.LogAspect;
import com.wenda.wenda.async.EventModel;
import com.wenda.wenda.async.EventProducer;
import com.wenda.wenda.async.EventType;
import com.wenda.wenda.model.Comment;
import com.wenda.wenda.model.EntityType;
import com.wenda.wenda.model.HostHolder;
import com.wenda.wenda.model.Question;
import com.wenda.wenda.service.CommentService;
import com.wenda.wenda.service.QuestionServive;
import com.wenda.wenda.util.WendaUtil;
import org.aspectj.weaver.Lint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);


    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionServive questionServive;
    @Autowired
    EventProducer eventProducer;
    @RequestMapping(path = {"/addComment"},method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            if (hostHolder.getUsers() != null) {
                comment.setUserId(hostHolder.getUsers().getId());
            } else {
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
                //return "redirect:/reglogin";
            }
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            commentService.addComment(comment);
            int commentCount = commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            questionServive.updateCommentCount(comment.getEntityId(),commentCount);
            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId())
                    .setEntityId(questionId));


        }catch (Exception e){
            logger.error("增加评论失败");

        }

        return "redirect:/question/"+questionId;
    }
}
