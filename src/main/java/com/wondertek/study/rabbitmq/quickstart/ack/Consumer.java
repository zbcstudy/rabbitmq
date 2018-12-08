package com.wondertek.study.rabbitmq.quickstart.ack;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wondertek.study.rabbitmq.quickstart.Constance;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public static void main(String[] args) {
        try {
            Connection connection = Constance.getConnection();
            Channel channel = connection.createChannel();

            String exchangeName = "test_ack_exchange";
            String exchangType = "topic";
            String rountingKey = "ack.*";
            String queueName = "test_ack_queue";

            //声明exchange
            channel.exchangeDeclare(exchangeName, exchangType, true);
            //声明queue
            channel.queueDeclare(queueName, true, false, false, null);
            //消息队列与交换机进行绑定
            channel.queueBind(queueName, exchangeName, rountingKey);
//            channel.basicQos(0,1,false);

            channel.basicConsume(queueName, false, new MyConsumer(channel));


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
