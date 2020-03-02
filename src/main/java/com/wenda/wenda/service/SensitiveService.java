package com.wenda.wenda.service;

import com.wenda.wenda.aspect.LogAspect;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import sun.java2d.pipe.hw.AccelTypedVolatileImage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null){
                addWord(lineTxt.trim());
            }
            reader.close();
        }catch (Exception e){
            logger.error("读取敏感词文件失败"+e.getMessage());

        }

    }
    //添加敏感词
    private void addWord(String linetext){
        TriNode tempNode = rootNode;
        for (int i = 0; i < linetext.length(); i++) {
            Character c = linetext.charAt(i);
            if (isSymbol(c)){
                continue;
            }

            TriNode node = tempNode.getSubNode(c);
            if (node == null){
                node = new TriNode();
                tempNode.addSubNode(c,node);
            }
            tempNode = node;
            if (i == linetext.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    private class TriNode{
        //是不是关键词的结尾
        private  boolean end = false;
        //当前节点下的子节点 例如但钱节点为a 子节点有 q w e
        private Map<Character,TriNode> subNode = new HashMap<Character, TriNode>();

        public void addSubNode(Character key,TriNode triNode){
            subNode.put(key,triNode);
        }
        TriNode getSubNode(Character key){
            return subNode.get(key);
        }
        boolean isKeyWordEnd(){
            return end;
        }
        void setKeywordEnd(boolean end){
            this.end = end;
        }
    }
    private TriNode rootNode = new TriNode();
    //非英文过滤
    private boolean isSymbol(char c){
        int ic = (int)c;
        return !CharUtils.isAsciiAlphanumeric(c) && (ic<0x2E80 || ic>0x9FFF);
    }

    //过滤
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return text;
        }
        StrBuilder result = new StrBuilder();
        TriNode tempNode =rootNode;
        int begin = 0;
        int position = 0;
        String replacecement = "***";
        while (position<text.length()){
            char c = text.charAt(position);
            if (isSymbol(c)){
                if (tempNode == rootNode){
                    result.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            if (tempNode == null){
                result.append(text.charAt(begin));
                position = begin+1;
                begin = position;
                tempNode = rootNode;
                //发现敏感词
            }else if (tempNode.isKeyWordEnd()){
                result.append(replacecement);
                position = position+1;
                begin = position;
                tempNode = rootNode;
            }else {
                ++position;

            }
        }
        result.append(text.substring(begin));
        return result.toString();



    }


    public static void main(String[] args) {
        SensitiveService sensitiveService = new SensitiveService();
        sensitiveService.addWord("色情");
        sensitiveService.addWord("李明");
        System.out.println(sensitiveService.filter("你 好色 情"));
    }
}
