package com.wenda.wenda.controller;

import com.wenda.wenda.aspect.LogAspect;
import com.wenda.wenda.async.EventModel;
import com.wenda.wenda.async.EventProducer;
import com.wenda.wenda.async.EventType;
import com.wenda.wenda.model.*;
import com.wenda.wenda.service.*;
import com.wenda.wenda.util.WendaUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);


    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionServive questionServive;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    SearchService searchService;
    @Autowired
    FollowService followService;
    @Autowired
    UserServive userServive;


    @RequestMapping(path = {"/search"}, method = {RequestMethod.GET})
    public String search(Model model, @RequestParam("q") String keyword,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "10") int count) {
//        searchService.searchQuestion("上网",0,100,"<h>","</h>");
        try {
            List<Question> questionList = searchService.searchQuestion(keyword, offset, count, "<em>", "</em>");
            List<ViewObject> vos = new ArrayList<>();
            for (Question question : questionList) {
                Question q = questionServive.selectById(question.getId());
                ViewObject vo = new ViewObject();
                if (question.getContent() != null){
                    q.setContent(question.getContent());
                }
                if (question.getTitle() != null){
                    q.setTitle(question.getTitle());
                }
                vo.set("question", q);
                vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                vo.set("user", userServive.getUser(q.getUserId()));
                vos.add(vo);
            }
            model.addAttribute("vos",vos);
        } catch (IOException e) {
            logger.error("搜索失败"+e.getMessage());
        } catch (SolrServerException e) {
            logger.error("搜索失败"+e.getMessage());
        }
        return "result";
    }
}
