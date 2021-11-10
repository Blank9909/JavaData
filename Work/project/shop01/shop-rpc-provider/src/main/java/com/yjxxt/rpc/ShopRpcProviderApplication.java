package com.yjxxt.rpc;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yjxxt.rpc.mapper")
public class ShopRpcProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopRpcProviderApplication.class,args);
    }
}
