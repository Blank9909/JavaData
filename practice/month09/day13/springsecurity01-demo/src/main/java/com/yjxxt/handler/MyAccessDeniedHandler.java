package com.yjxxt.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
       /* httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletRequest.setCharacterEncoding("utf-8");
        PrintWriter out = httpServletResponse.getWriter();
        String result="{\"code\":403,\"massage\":\"暂无访问权限\"}";
        out.flush();
        out.close();*/

        httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/403.html");
    }
}
