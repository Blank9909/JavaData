package com.yjxxt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //关闭csrf
        http.csrf().disable();

        http.formLogin()
                //自定义登录页面
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                //登录成功后跳转页面
                .successForwardUrl("/toMain")
                //登录失败后跳转页面
                .failureForwardUrl("/toError")
                //配置自定义表单登录参数
                .usernameParameter("userName")
                .passwordParameter("userPwd");

        http.authorizeRequests()
                .antMatchers("/login.html","/error.html").permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
