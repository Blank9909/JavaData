package com.yjxxt.config;

import com.yjxxt.handler.LoginFailedHandler;
import com.yjxxt.handler.LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

//@Configuration
public class SecurityConfig02 extends WebSecurityConfigurerAdapter {

    @Resource
    private LoginFailedHandler loginFailedHandler;

    @Resource
    private LoginSuccessHandler loginSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //关闭csrf
        http.csrf().disable();

        http.formLogin()
                //自定义登录页面
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                //自定义成功与失败页面
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailedHandler)
                //登录成功后跳转页面
                //.successForwardUrl("/toMain")
                //登录失败后跳转页面
                //.failureForwardUrl("/toError")
                //配置自定义表单登录参数
                .usernameParameter("userName")
                .passwordParameter("userPwd");

        http.authorizeRequests()
                //配置放行路径
                .antMatchers("/login.html","/error.html","/js/**","/css/**","/images/**").permitAll()
                //告诉SpringSecurity，需要登录才能访问
                .anyRequest().authenticated();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
