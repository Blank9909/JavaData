package com.yjxxt.springsecurityouth2.service;

import com.yjxxt.springsecurityouth2.bean.User;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class UserService implements UserDetailsService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //对密码进行加密
        //对密码加密
        String password = passwordEncoder.encode("123456");
        //构造user对象，并将密码放里边
        return new User("admin",password, AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}
