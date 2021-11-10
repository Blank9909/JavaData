package com.yjxxt;

import com.netflix.loadbalancer.RandomRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication

public class ConsumerApplication {

    @Bean
    public RandomRule randomRule() {
        return new RandomRule();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static void main( String[] args ) {
        SpringApplication.run(ConsumerApplication.class,args);
    }
}
