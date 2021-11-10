package com.yjxxt.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class LoginFailedHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        //设置编码
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletRequest.setCharacterEncoding("utf-8");
        //输出流
        PrintWriter out = httpServletResponse.getWriter();
        String result="{\"code\":400,\"massage\":\"用户登录失败\"}";
        out.write(result);
        out.flush();
        out.close();
    }
}
