package com.yjxxt.work.fair.recv;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 工作队列-公平分发-消息接收者
 */
public class Recv02 {
    // 队列名称
    private final static String QUEUE_NAME = "work_fair";
    public static void main(String[] args) {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.203.131");
        factory.setPort(5672);
        factory.setUsername("shop");
        factory.setPassword("shop");
        factory.setVirtualHost("/shop");
        try {
            // 通过工厂创建连接
            final Connection connection = factory.newConnection();
            // 获取通道
            final Channel channel = connection.createChannel();
            // 指定队列
            channel.queueDeclare(QUEUE_NAME, false, false, false,null);
            /*
            限制RabbitMQ只发不超过1条的消息给同一个消费者。
            当消息处理完毕后，有了反馈，才会进行第二次发送。
            */
            int prefetchCount = 1;
            channel.basicQos(prefetchCount);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            // 获取消息
            DeliverCallback deliverCallback = (consumerTag, delivery)-> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received02 '" + message + "'");
                // 模拟程序执行所耗时间
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 手动回执消息
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };
            // 监听队列
            /*
            autoAck = true代表自动确认消息
            autoAck = false代表手动确认消息
            */
            boolean autoAck = false;
            channel.basicConsume(QUEUE_NAME, autoAck,deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
