package com.wondertek.study.rabbitmq.quickstart;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) {
        Connection connection = null;
        Channel channel = null;
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
            AMQP.Confirm.SelectOk selectOk = channel.confirmSelect();

            Map<String, Object> headers = new HashMap<>();
            headers.put("msg1", "message1");
            headers.put("msg2", "message2");
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)
                    .expiration("10000")
                    .contentEncoding("UTF-8")
                    .headers(headers)
                    .build();

            String message = "hello rabbitmq";
            //4 通过channel发送数据
            channel.basicPublish("","test001",basicProperties,message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
