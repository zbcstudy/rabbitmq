package com.wondertek.study.rabbitmq.quickstart.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wondertek.study.rabbitmq.quickstart.Constance;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer4DirectExechange {

    public static void main(String[] args) {
        try {
            Connection connection = Constance.getConnection();
            Channel channel = connection.createChannel();

            //声明
            String exechangeName = "test_direct_exechange";
            String rountingKey = "test.direct";

            String message = "HelloWorld RabbitMq 4 direct exechange";
            channel.basicPublish(exechangeName, rountingKey, null, message.getBytes());
            Constance.closeSource(channel,connection);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
