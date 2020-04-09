package com.wenda.wenda.service;

import com.wenda.wenda.dao.QuestionDao;
import com.wenda.wenda.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionServive {
    @Autowired
    QuestionDao questionDao;
    @Autowired
    SensitiveService sensitiveService;


    /**
     * 查找问题
     * @param id
     * @return
     */
    public Question selectById(int id){
        return questionDao.selectbyId(id);
    }

    /**
     * 获取问题
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Question> getlatestQuestion(int userId,int offset,int limit){
        return questionDao.selectLatestQuestions(userId,offset,limit);
    }

    /**
     * 添加问题
     * @param question
     * @return
     */
    public int addQuestion(Question question){
        //html过滤
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        //敏感词过滤
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle() ));


        return questionDao.addQuestion(question)>0? question.getId() : 0;
    }

    public int updateCommentCount(int id, int count) {
        return questionDao.updateCommentCount(id, count);
    }
}
