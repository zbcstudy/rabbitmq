package com.wondertek.study.rabbitmq.quickstart.selfconsumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wondertek.study.rabbitmq.quickstart.Constance;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 自定义消费者进行监听
 */
public class Producer {

    public static void main(String[] args) {
        try {
            Connection connection = Constance.getConnection();

            Channel channel = connection.createChannel();

            //指定消息的确认模式
            channel.confirmSelect();
            String exchangeName = "test_consumer_exchange";
            String rountingKey = "consumer.save";
            String rountingKeyError = "abs.save";
            String message = "rabbitmq send return message";
            for (int i = 0; i < 10; i++) {
                channel.basicPublish(exchangeName, rountingKey, true,null, message.getBytes());
            }
//            channel.basicPublish(exchangeName, rountingKeyError, true,null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
