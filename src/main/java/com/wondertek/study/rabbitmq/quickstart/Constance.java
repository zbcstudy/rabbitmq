package com.wondertek.study.rabbitmq.quickstart;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Constance {

    public static final String DEFAULT_HOST = "192.168.115.128";
    public static final int DEFAULT_PORT = 5672;
    public static final String DEFAULT_VIRTUALHOST = "/";

    /**
     * 获取RabbitMQ连接
     * @param host
     * @param port
     * @param vhost
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    public static Connection getConnection(String host, int port, String vhost) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setVirtualHost(vhost);
        //设置重连机制
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(3000);
        Connection connection = connectionFactory.newConnection();
        return connection;
    }

    public static Connection getConnection() throws IOException, TimeoutException {
        return getConnection(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_VIRTUALHOST);
    }

    /**
     * 关闭资源
     * @param channel
     * @param connection
     * @throws IOException
     * @throws TimeoutException
     */
    public static void closeSource(Channel channel, Connection connection) throws IOException, TimeoutException {
        if (channel != null) {
            channel.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}
