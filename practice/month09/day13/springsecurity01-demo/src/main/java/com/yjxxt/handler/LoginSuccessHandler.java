package com.yjxxt.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        System.out.println(user.getUsername()+","+user.getPassword());
        user.getAuthorities().forEach(a->{
            System.out.println(a.getAuthority());
        });
        httpServletResponse.setContentType("application/json;charset:utf-8");
        httpServletRequest.setCharacterEncoding("utf-8");
        PrintWriter out = httpServletResponse.getWriter();
        String result="{\"code\":200,\"massage\":\"用户登录成功\"}";
        out.write(result);
        out.flush();
        out.close();
    }
}
