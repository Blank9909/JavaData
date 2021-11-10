package com.yjxxt.eurake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
//开启EurekaServer注解
@EnableEurekaServer
public class EurekaService02Application {

    public static void main(String[] args) {
        SpringApplication.run(EurekaService02Application.class,args);
    }
}
