package com.yjxxt.config;

import com.yjxxt.handler.LoginFailedHandler;
import com.yjxxt.handler.LoginSuccessHandler;
import com.yjxxt.handler.MyAccessDeniedHandler;
import com.yjxxt.handler.MyLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
public class SecurityConfig03 extends WebSecurityConfigurerAdapter {

    @Resource
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private LoginFailedHandler loginFailedHandler;

    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;

    @Resource
    private DataSource dataSource;

    @Resource
    private UserDetailsService userDetailsService;

    @Autowired
    private MyLogoutSuccessHandler myLogoutSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //表单信息
        http.formLogin()
                //登录页面
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                //登录成功页面
                .successHandler(loginSuccessHandler)
                //登录失败页面
                .failureHandler(loginFailedHandler)
                //配置自定义表单参数
                .passwordParameter("userName")
                .usernameParameter("userPwd");

        //禁用csrf
        http.csrf().disable();

        //放行页面
        http.authorizeRequests()
                //放行路径
                .antMatchers("/login.html","/error.html","/js/**","/css/**","/images/**").permitAll()
                //所有的请求登录才能访问
                .anyRequest().authenticated();

        //403没有访问权限
        http.exceptionHandling()
                .accessDeniedHandler(myAccessDeniedHandler);

        //免登录配置
        http.rememberMe()
                //登录逻辑交给哪一个对象
                .userDetailsService(userDetailsService)
                //持久层对象
                .tokenRepository(getPersistentTokenRepository())
                //令牌有效期
                .tokenValiditySeconds(60*60*24*7)
                //记住cookie名称
                .rememberMeCookieName("rmv")
                //记住我的参数
                .rememberMeParameter("rmv");

        //用户退出
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(myLogoutSuccessHandler);
    }

    @Bean
    public PersistentTokenRepository getPersistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        //自动建表，第一次启动时需要，第二次启动时注释掉
        //jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
