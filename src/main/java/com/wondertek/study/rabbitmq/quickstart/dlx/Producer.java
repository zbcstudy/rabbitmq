package com.wondertek.study.rabbitmq.quickstart.dlx;

import com.rabbitmq.client.AMQP;
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
            String exchangeName = "test_dlx_exchange";
            String rountingKey = "dlx.save";
            String message = "rabbitmq send return message";
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)
                    .contentEncoding("UTF-8")
                    .expiration("10000")
                    .build();
            channel.basicPublish(exchangeName, rountingKey, true,properties, message.getBytes());
//            channel.basicPublish(exchangeName, rountingKeyError, true,null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
