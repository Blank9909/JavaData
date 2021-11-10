package com.yjxxt.mail;

import com.yjxxt.mail.service.IMailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class TestMailService {

    @Resource
    private IMailService mailService;


    @Test
    public  void test(){
        mailService.sendMail();
    }


}
