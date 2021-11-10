package com.yjxxt.simple.send;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 简单模式队列-消息发送者
 */
public class Send {
    //队列名称
    private final static String QUEUE_NAME="hello";

    public static void main(String[] args) {
        //创建链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.203.131");
        factory.setPort(5672);
        factory.setUsername("shop");
        factory.setPassword("shop");
        factory.setVirtualHost("/shop");
        Connection connection=null;
        Channel channel=null;
        try {
            //通过工厂创建连接
            connection = factory.newConnection();
            //获取通道
            channel = connection.createChannel();
            //声明队列
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            //创建消息
            String message="Hello Word!";
            //将消息放入队列
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes("UTF-8"));
            System.out.println("[x] Sent'"+message+"'");
        }catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
            try {
                //关闭通道
                if(channel!=null && channel.isOpen()){
                    channel.close();
                }
                if(connection!=null&&connection.isOpen()){
                    connection.close();
                }
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }

        }

    }

}
