package com.yjxxt.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    //方法名称必须是redisTemplate，相当于覆盖自动化配置中的序列化模式
    @Bean
    public RedisTemplate<String,Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        //为string类型key设置序列器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //为string类型value设置序列器
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //为hash类型key设置序列器
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //为hash类型value设置序列器
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }


   /* @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration(){
        RedisSentinelConfiguration sentinelConfig = new
                RedisSentinelConfiguration()
// 主节点名称
                .master("mymaster")
// 主从服务器地址
                .sentinel("192.168.10.100", 26379)
                .sentinel("192.168.10.100", 26380)
                .sentinel("192.168.10.100", 26381);
// 设置密码
        sentinelConfig.setPassword("root");
        return sentinelConfig;
    }
*/
}
