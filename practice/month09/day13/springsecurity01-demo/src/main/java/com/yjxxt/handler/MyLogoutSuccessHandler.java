package com.yjxxt.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        //设置编码
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletRequest.setCharacterEncoding("utf-8");
        //输出
        PrintWriter out = httpServletResponse.getWriter();
        String result="{\"code\":200,\"massage\":\"用户退出登录成功\"}";
        out.write(result);
        out.flush();
        out.close();
    }
}
