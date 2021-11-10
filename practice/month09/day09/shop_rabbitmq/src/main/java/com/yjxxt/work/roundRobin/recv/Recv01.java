package com.yjxxt.work.roundRobin.recv;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 工作模式队列-轮询分发-消息接收者
 */

public class Recv01 {
    //队列名称
    private final static String QUEUE_NAME="work_roundRobin";

    public static void main(String[] args) {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.203.131");
        factory.setPort(5672);
        factory.setUsername("shop");
        factory.setPassword("shop");
        factory.setVirtualHost("/shop");
        try {
            // 通过工厂创建连接
            Connection connection = factory.newConnection();
            // 获取通道
            Channel channel = connection.createChannel();
            // 指定队列
            channel.queueDeclare(QUEUE_NAME, false, false,false,null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
                    // 获取消息
                    DeliverCallback deliverCallback = (consumerTag, delivery)-> {
                        String message = new String(delivery.getBody(), "UTF-8");
                        System.out.println(" [x] Received01 '" + message + "'");
                        // 模拟程序执行所耗时间
                        try {
                            //睡眠1秒钟
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
