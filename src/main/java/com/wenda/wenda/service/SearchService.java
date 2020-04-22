package com.wenda.wenda.service;

import com.wenda.wenda.model.Question;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    @Autowired
    QuestionServive questionServive;
    /**
     * 搜索问题
     * @param keyWord
     * @param offset
     * @param count
     * @param hlPre 高亮
     * @param hlPos
     * @return
     */
    private static final String QUESTION_TITLE_FIELD = "question_title";
    private static final String QUESTION_CONTENT_FIELD = "question_content";
    final String solrUrl = "http://localhost:8983/solr/wenda";
    final String solrCore = "solr_core";
    HttpSolrClient client = new HttpSolrClient.Builder(solrUrl)
            .withConnectionTimeout(10000)
            .withSocketTimeout(60000)
            .build();

    /**
     * 查找问题
     * @param keyWord 查找的关键词
     * @param offset 开始起点
     * @param count 搜索的总数
     * @param hlPre 高亮的开头
     * @param hlPos 高亮的结尾
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public List<Question> searchQuestion(String keyWord,int offset,int count,String hlPre,String hlPos) throws IOException, SolrServerException {

        //查询
        SolrQuery solrQuery = new SolrQuery(keyWord);
        solrQuery.setRows(count);
        solrQuery.setStart(offset);
        solrQuery.setHighlight(true);
        solrQuery.setHighlightSimplePre(hlPre);
        solrQuery.setHighlightSimplePost(hlPos);
        solrQuery.set("fl","*");
        solrQuery.set("df",QUESTION_CONTENT_FIELD);
        solrQuery.set("hl.fl",QUESTION_CONTENT_FIELD+','+QUESTION_TITLE_FIELD);
        List<Question> questionList = new ArrayList<>();

        QueryResponse response = client.query(solrQuery);
        System.out.println(response.getHighlighting());
        for (Map.Entry<String ,Map<String,List<String>>> entry:response.getHighlighting().entrySet()){
            Question question = new Question();
            question.setId(Integer.parseInt(entry.getKey()));
            if (entry.getValue().containsKey(QUESTION_CONTENT_FIELD)){
                List<String> content = entry.getValue().get(QUESTION_CONTENT_FIELD);
                if (content.size()>0){
                    question.setContent(content.get(0));
                }
            }
            if (entry.getValue().containsKey(QUESTION_TITLE_FIELD)){
                List<String> title = entry.getValue().get(QUESTION_TITLE_FIELD);
                if (title.size()>0){
                    question.setTitle(title.get(0));
                }
            }
            questionList.add(question);

        }

        return questionList;

    }

    public boolean indexQuestion(int qid,String title,String content) throws IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id",qid);
        doc.setField(QUESTION_CONTENT_FIELD,content);
        doc.setField(QUESTION_TITLE_FIELD,title);
        UpdateResponse response = client.add(doc,1000);
        return response !=null && response.getStatus() == 0;
    }
}
