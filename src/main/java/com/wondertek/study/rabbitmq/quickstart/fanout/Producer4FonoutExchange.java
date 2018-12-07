package com.wondertek.study.rabbitmq.quickstart.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wondertek.study.rabbitmq.quickstart.Constance;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer4FonoutExchange {
    public static void main(String[] args) {
        try {
            Connection connection = Constance.getConnection();
            Channel channel = connection.createChannel();

            //声明
            String exechangeName = "test_fanout_exechange";
            String rountingKey = "anything";

            String message = "HelloWorld RabbitMq 4 direct exechange";
            for (int i = 0; i < 10; i++) {
                channel.basicPublish(exechangeName, rountingKey, null, message.getBytes());
            }
            Constance.closeSource(channel,connection);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
