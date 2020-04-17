//package com.wenda.wenda.util;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeUtility;
//import java.io.StringReader;
//import java.util.Map;
//import java.util.Properties;
//
///**
// * Created by nowcoder on 2016/7/15. // course@nowcoder.com NKnk66
// */
//@Service
//public class MailSender implements InitializingBean {
//    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
//    private JavaMailSenderImpl mailSender;
//
//    @Autowired
////    private VelocityEngine velocityEngine;
//
//    /**
//     * 发送邮件
//     * @param to 发给谁
//     * @param subject 标题
//     * @param template 模板·
//     * @param model 模板字符串
//     * @return
//     */
////    public boolean sendWithHTMLTemplate(String to, String subject,
////                                        String template, Map<String, Object> model) {
////        try {
////            String nick = MimeUtility.encodeText("牛客中级课");
////            InternetAddress from = new InternetAddress(nick + "<course@nowcoder.com>");
////            MimeMessage mimeMessage = mailSender.createMimeMessage();
////            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
////            String result = "";
//////            String result = VelocityEngineUtils
//////                    .mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);
////            mimeMessageHelper.setTo(to);
////            mimeMessageHelper.setFrom(from);
////            mimeMessageHelper.setSubject(subject);
////            mimeMessageHelper.setText(result, true);
////            mailSender.send(mimeMessage);
////            return true;
////        } catch (Exception e) {
////            logger.error("发送邮件失败" + e.getMessage());
////            return false;
////        }
////    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        mailSender = new JavaMailSenderImpl();
//        mailSender.setUsername("1040147144");
//        mailSender.setPassword("147258369");
//        mailSender.setHost("smtp.qq.com");
//        //mailSender.setHost("smtp.qq.com");
//        mailSender.setPort(465);
//        mailSender.setProtocol("smtps");
//        mailSender.setDefaultEncoding("utf8");
//        Properties javaMailProperties = new Properties();
//        javaMailProperties.put("mail.smtp.ssl.enable", true);
//        //javaMailProperties.put("mail.smtp.auth", true);
//        //javaMailProperties.put("mail.smtp.starttls.enable", true);
//        mailSender.setJavaMailProperties(javaMailProperties);
//    }
//}
