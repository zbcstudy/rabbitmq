package com.wondertek.study.rabbitmq.quickstart;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
    private static final String DEFAULT_HOST = "192.168.115.128";
    private static final int DEFAULT_PORT = 5672;
    private static final String DEFAULT_VIRTUALHOST = "/";


    public static void main(String[] args) {
        Connection connection = null;
        Channel channel = null;
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
            String message = "hello rabbitmq";
            //4 通过channel发送数据
            channel.basicPublish("","test001",null,message.getBytes());
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
