package com.yjxxt.portal.interceptors;

import com.yjxxt.common.result.CookieUtil;
import com.yjxxt.sso.pojo.Admin;
import com.yjxxt.sso.service.ISSOService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
@Component
public class TicketValidateInterceptor implements HandlerInterceptor {

    @Reference(interfaceClass = ISSOService.class,version = "1.0")
    private ISSOService ssoService;

    private RedisTemplate template;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        */
/**
         * 1.获取票据ticket
         * 2.校验票据
         *    Admin admin = sso.validateTicket(ticket);
         *  判断admin ==null?
         *//*

       String ticket =  CookieUtil.getCookieValue(request,"userTicket");
       Admin admin= ssoService.validateTicket(ticket);
       if(admin==null){
           // 用户未登录或登录已失效
           response.sendRedirect(request.getContextPath()+"/login");
           return false;
       }

       // 延迟ticket 失效时间
       ssoService.expireTicket(ticket);
       request.getSession().setAttribute("user",admin);
        return true;
    }
}
*/
