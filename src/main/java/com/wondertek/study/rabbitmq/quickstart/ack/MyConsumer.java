package com.wondertek.study.rabbitmq.quickstart.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class MyConsumer extends DefaultConsumer {

    private Channel channel;

    public MyConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("=========== consumer message =============");
        System.out.println("body: " + new String(body));
        try {
            Thread.sleep(2000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if ((Integer) properties.getHeaders().get("num") == 0) {
            /**
             * requeue:消息签收失败，是否将消息回退到消息队列
             */
            channel.basicNack(envelope.getDeliveryTag(), false, true);
        }else {
            /**
             * 消费端设置限流：手动签收消息，消息签收完成之后，通知broker消息已经被消费
             * multiple:设置是否批量签收
             */
            channel.basicAck(envelope.getDeliveryTag(), false);
        }
    }
}
