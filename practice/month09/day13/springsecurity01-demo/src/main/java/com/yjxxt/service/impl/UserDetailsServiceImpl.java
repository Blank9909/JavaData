package com.yjxxt.service.impl;

import org.apache.catalina.authenticator.NonLoginAuthenticator;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        //判断用户是否存在
        if(!"admin".equals(userName)){
            throw new UsernameNotFoundException("用户不存在");
        }

        //密码加密
        String password = encoder.encode("123");

        //构造user，并返回user
        return new User(userName,password,
                AuthorityUtils.commaSeparatedStringToAuthorityList("1010"));
    }
}
