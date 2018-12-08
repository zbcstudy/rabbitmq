package com.wondertek.study.rabbitmq.quickstart.selfconsumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.wondertek.study.rabbitmq.quickstart.Constance;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public static void main(String[] args) {
        try {
            Connection connection = Constance.getConnection();
            Channel channel = connection.createChannel();

            String exchangeName = "test_consumer_exchange";
            String exchangType = "topic";
            String rountingKey = "consumer.*";
            String queueName = "test_consumer_queue";

            //声明exchange
            channel.exchangeDeclare(exchangeName, exchangType, true);
            //声明queue
            channel.queueDeclare(queueName, true, false, false, null);
            //消息队列与交换机进行绑定
            channel.queueBind(queueName, exchangeName, rountingKey);

            channel.basicConsume(queueName, true, new MyConsumer(channel));


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
