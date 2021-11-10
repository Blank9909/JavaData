package com.yjxxt.manager.controller;


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



    @Reference(interfaceClass = ISSOService.class,version = "1.0")
    private ISSOService ssoService;



    @RequestMapping("doLogin")
    @ResponseBody
    public BaseResult doLogin(String userName, String password,String imageCode,
                              HttpServletRequest request, HttpServletResponse response){

        if (StringUtils.isBlank(imageCode)) {
           return BaseResult.error("请输入验证码!");
        }
        String sessionImageCode = (String) request.getSession().getAttribute("pictureVerifyKey");
        if(!sessionImageCode.equals(imageCode)){
            return BaseResult.error("验证码错误!");
        }
        request.getSession().removeAttribute("pictureVerifyKey");
        String ticket = ssoService.login(userName,password);
       if(StringUtils.isNotBlank(ticket)){
           CookieUtil.setCookie(request,response,"userTicket",ticket);
           return BaseResult.success();
       }
       return BaseResult.error("用户名或密码错误!");
    }


    @RequestMapping("logout")
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
    }
}
