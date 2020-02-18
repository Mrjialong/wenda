package com.wenda.wenda.service;

import com.wenda.wenda.dao.QuestionDao;
import com.wenda.wenda.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServive {
    @Autowired
    QuestionDao questionDao;

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
}
