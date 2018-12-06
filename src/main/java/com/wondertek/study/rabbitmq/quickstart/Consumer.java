package com.wondertek.study.rabbitmq.quickstart;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {

    private static final String DEFAULT_HOST = "192.168.115.128";
    private static final int DEFAULT_PORT = 5672;
    private static final String DEFAULT_VIRTUALHOST = "/";


    public static void main(String[] args) {
        Connection connection;
        Channel channel;
        QueueingConsumer queueingConsumer;
        //1 创建factory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(DEFAULT_HOST);
        connectionFactory.setPort(DEFAULT_PORT);
        connectionFactory.setVirtualHost(DEFAULT_VIRTUALHOST);
        try {
            //2 创建连接
            connection = connectionFactory.newConnection();

            //3 根据连接创建channel
            channel = connection.createChannel();

            String queueName = "test001";
            //4 根据channel创建一个队列
            channel.queueDeclare(queueName, true, false, false, null);

            //5 创建消费者
            queueingConsumer = new QueueingConsumer(channel);

            //6 设置channel
            channel.basicConsume(queueName, true, queueingConsumer);

            //7 获取消息
            while (true) {
               QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
               System.out.println("消费端获取数据：" + delivery.getBody().toString());
//                Envelope envelope = delivery.getEnvelope();
            }
        }catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}