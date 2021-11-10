package com.yjxxt.security;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class MyTest {

    @Test
    public void test00(){
        //创建解析器
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        //对密码加密
        String encode = encoder.encode("123456");
        System.out.println(encode);

        //判断原字符和加密后的字符是否匹配
        boolean matches = encoder.matches("123456", "$2a$10$74NuFa8b4DE4IkEBccMAh.oMU/GlLnCnmfGFyffl0lg.Uypky/Oxi");
        System.out.println(matches);

    }

}
