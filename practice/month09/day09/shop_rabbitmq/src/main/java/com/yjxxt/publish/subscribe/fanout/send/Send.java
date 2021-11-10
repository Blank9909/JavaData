package com.yjxxt.publish.subscribe.fanout.send;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发布与订阅模式队列-fanout广播模式-消息发送者
 */
public class Send {
// 队列名称
// 如果不声明队列，会使用默认值，RabbitMQ会创建一个排他队列，连接断开后自动删除
    //private final static String QUEUE_NAME = "ps_fanout";
// 交换机名称
    private static final String EXCHANGE_NAME = "exchange_fanout";

    public static void main(String[] args) {
// 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.203.131");
        factory.setPort(5672);
        factory.setUsername("shop");
        factory.setPassword("shop");
        factory.setVirtualHost("/shop");
        Connection connection = null;
        Channel channel = null;
        try {
            // 通过工厂创建连接
            connection = factory.newConnection();
            // 获取通道
            channel = connection.createChannel();
            // 声明队列
            //channel.queueDeclare(QUEUE_NAME, false, false, false,null);
            // 绑定交换机 fanout：广播模式
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
            // 创建消息，模拟发送手机号码和邮件地址
            String message = "18600002222|12345@qq.com";
            // 将产生的消息发送至交换机
            channel.basicPublish(EXCHANGE_NAME, "", null,message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭通道
                if (null != channel && channel.isOpen())
                    channel.close();
                // 关闭连接
                if (null != connection && connection.isOpen())
                    connection.close();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
