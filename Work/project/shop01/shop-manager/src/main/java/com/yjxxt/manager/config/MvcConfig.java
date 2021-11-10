package com.yjxxt.manager.config;

import com.yjxxt.manager.interceptors.TicketValidateInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private TicketValidateInterceptor ticketValidateInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ticketValidateInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/bootstrap/**","/css/**","/dist/**","/html/**")
                .excludePathPatterns("/images/**","/img/**","/js/**","/plugins/**")
                .excludePathPatterns("/user/doLogin","/user/logout","/image/getKaptchaImage","/login");
    }
}
