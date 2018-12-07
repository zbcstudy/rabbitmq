package com.wondertek.study.rabbitmq.quickstart;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Consumer {

    public static void main(String[] args) {
        Connection connection;
        Channel channel;
        QueueingConsumer queueingConsumer;
        //1 创建factory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Constance.DEFAULT_HOST);
        connectionFactory.setPort(Constance.DEFAULT_PORT);
        connectionFactory.setVirtualHost(Constance.DEFAULT_VIRTUALHOST);
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
                Map<String, Object> headers = delivery.getProperties().getHeaders();
                for (Object o : headers.values()) {
                    System.out.println(o);
                }
                System.out.println("消费端获取数据：" + new String(delivery.getBody()));
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