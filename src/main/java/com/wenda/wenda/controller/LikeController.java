package com.wenda.wenda.controller;

import com.wenda.wenda.async.EventModel;
import com.wenda.wenda.async.EventProducer;
import com.wenda.wenda.async.EventType;
import com.wenda.wenda.model.Comment;
import com.wenda.wenda.model.EntityType;
import com.wenda.wenda.model.HostHolder;
import com.wenda.wenda.service.CommentService;
import com.wenda.wenda.service.LikeService;
import com.wenda.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
    public class LikeController {
        @Autowired
        LikeService likeService;

        @Autowired
        HostHolder hostHolder;

        @Autowired
        CommentService commentService;

        @Autowired
        EventProducer eventProducer;

//

        @RequestMapping(path = {"/like"}, method = {RequestMethod.POST})
        @ResponseBody
        public String like(@RequestParam("commentId") int commentId) {
            if (hostHolder.getUsers() == null) {
                return WendaUtil.getJSONString(999);
            }
            Comment comment = commentService.getCommentById(commentId);

            eventProducer.fireEvent(new EventModel(EventType.LIKE)
                    .setActorId(hostHolder.getUsers().getId()).setEntityId(commentId)
                    .setEntityType(EntityType.ENTITY_COMMENT).setEntityOwnerId(comment.getUserId())
                    .setExt("questionId",String.valueOf(comment.getEntityId())));

//            Comment comment = commentService.getCommentById(commentId);

//            eventProducer.fireEvent(new EventModel(EventType.LIKE)
//                    .setActorId(hostHolder.getUser().getId()).setEntityId(commentId)
//                    .setEntityType(EntityType.ENTITY_COMMENT).setEntityOwnerId(comment.getUserId())
//                    .setExt("questionId", String.valueOf(comment.getEntityId())));

            long likeCount = likeService.like(hostHolder.getUsers().getId(), EntityType.ENTITY_COMMENT, commentId);
            return WendaUtil.getJSONString(0, String.valueOf(likeCount));
        }

        @RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
        @ResponseBody
        public String dislike(@RequestParam("commentId") int commentId) {
            if (hostHolder.getUsers() == null) {
                return WendaUtil.getJSONString(999);
            }
            long likeCount = likeService.dislike(hostHolder.getUsers().getId(), EntityType.ENTITY_COMMENT, commentId);
            return WendaUtil.getJSONString(0, String.valueOf(likeCount));
        }
    }