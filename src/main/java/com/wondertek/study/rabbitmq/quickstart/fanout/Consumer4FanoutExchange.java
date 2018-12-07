package com.wondertek.study.rabbitmq.quickstart.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.wondertek.study.rabbitmq.quickstart.Constance;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer4FanoutExchange {
    public static void main(String[] args) {
        try {
            Connection connection = Constance.getConnection();
            Channel channel = connection.createChannel();

            //声明
            String exechangeName = "test_fanout_exechange";
            String exchangeType = "fanout";
            String queueName = "test_fonout_queue";

            String rountingKey = "lala";
            //声明一个交换机
            channel.exchangeDeclare(exechangeName, exchangeType, true, false, null);
            //声明一个队列
            channel.queueDeclare(queueName, false, false, false, null);
            //建立一个绑定关系
            channel.queueBind(queueName, exechangeName, rountingKey);
            QueueingConsumer consumer = new QueueingConsumer(channel);
            //参数：队列名称，ACk,Consumer
            channel.basicConsume(queueName, true, consumer);
            while (true) {
                //获取消息
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                String message = new String(delivery.getBody());
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
