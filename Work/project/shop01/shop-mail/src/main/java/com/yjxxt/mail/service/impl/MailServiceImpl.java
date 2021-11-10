package com.yjxxt.mail.service.impl;

import com.yjxxt.mail.service.IMailService;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class MailServiceImpl  implements IMailService {

    @Resource
    private JavaMailSenderImpl mailSender;

    @Override
    public void sendMail() {
        try {
            //创建邮件信息
            MimeMessage message= mailSender.createMimeMessage();
            //设置主题（标题）
            message.setSubject("spring_mail");
            // 创建带有附件的消息帮组类
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");
            //设置接收人
            helper.setTo(new InternetAddress("Blank0926@163.com"));
            //发送的信息
            helper.setText("Thank you for ordering!");
            //发送人
            helper.setFrom("Blank0926@163.com");
            //helper.addAttachment("JavaMail邮件发送.md", new File("C:\\Users\\lp\\Desktop\\JavaMail邮件发送.md"));
            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
