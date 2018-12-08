package com.wondertek.study.rabbitmq.quickstart.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wondertek.study.rabbitmq.quickstart.Constance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
            String exchangeName = "test_ack_exchange";
            String rountingKey = "ack.save";
            String rountingKeyError = "abs.save";
            Map<String, Object> headers = new HashMap<>();
            for (int i = 0; i < 5; i++) {

                headers.put("num", i);
                AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                        .contentEncoding("UTF-8")
                        .deliveryMode(2)
                        .headers(headers)
                        .build();
                String message = "rabbitmq send ack message" + i;
                channel.basicPublish(exchangeName, rountingKey, true,properties, message.getBytes());
            }
//            channel.basicPublish(exchangeName, rountingKeyError, true,null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
