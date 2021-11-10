package com.yjxxt.simple.recv;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 简单模式队列-消息接收者
 */
public class Recv {
    // 队列名称
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) {
// 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.203.131");
        factory.setPort(5672);
        factory.setUsername("shop");
        factory.setPassword("shop");
        factory.setVirtualHost("/shop");

        try {
            //通过工厂创建连接
            Connection connection = factory.newConnection();
            //获取通道
            Channel channel = connection.createChannel();
            //指定队列
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            //打印
            System.out.println("[*] Waiting for messages. To exit press CTRL+C");

            //获取信息
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [*] Received '" + message + "'");
            };

            // 监听队列
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}