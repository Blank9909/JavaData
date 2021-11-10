package com.yjxxt.mail.controller;

import com.yjxxt.mail.service.IMailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
public class MailController {

    @Resource
    private IMailService mailService;


    @RequestMapping("test")
    public  void test(){
        mailService.sendMail();
    }
}
