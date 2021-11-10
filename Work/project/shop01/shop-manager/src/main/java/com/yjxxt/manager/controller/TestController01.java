package com.yjxxt.manager.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController01 {


    @RequestMapping("u01")
    public String u01(){
        return "u01";
    }
}
