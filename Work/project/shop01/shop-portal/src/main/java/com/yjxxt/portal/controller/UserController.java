package com.yjxxt.portal.controller;


import com.yjxxt.common.result.BaseResult;
import com.yjxxt.common.result.CookieUtil;
import com.yjxxt.sso.service.ISSOService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("user")
public class UserController {


/*
    @Reference(interfaceClass = ISSOService.class,version = "1.0")
    private ISSOService ssoService;*/



   /* @RequestMapping("doLogin")
    @ResponseBody
    public BaseResult doLogin(String userName, String password,
                              HttpServletRequest request, HttpServletResponse response){
       String ticket = ssoService.login(userName,password);
       if(StringUtils.isNotBlank(ticket)){
           CookieUtil.setCookie(request,response,"userTicket",ticket);
           request.getSession().setAttribute("user",ssoService.validateTicket(ticket));
           return BaseResult.success();
       }
       return BaseResult.error("用户名或密码错误!");
    }*/



  /*  @RequestMapping("logout")
    public void logout(HttpServletRequest request,HttpServletResponse response){
        try {
            String ticket = CookieUtil.getCookieValue(request, "userTicket");
            ssoService.removeTicket(ticket);
            CookieUtil.deleteCookie(request,response,"userTicket");
            request.getSession().removeAttribute("user");
            response.sendRedirect(request.getContextPath()+"/login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
