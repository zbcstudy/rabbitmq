package com.wondertek.study.rabbitmq.transaction.listener;

import com.rabbitmq.client.Channel;
import com.wondertek.study.rabbitmq.transaction.util.MQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 死信队列监听器
 * @Author zbc
 * @Date 23:36-2019/3/1
 */
@Component
public class DeadLetterMessageListener implements ChannelAwareMessageListener {

    private Logger log = LoggerFactory.getLogger(DeadLetterMessageListener.class);

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        //获得消息体
        String messageBody = new String(message.getBody());

        log.warn("dead letter message:{} | tag:{} ", messageBody, messageProperties.getDeliveryTag());

        channel.basicAck(messageProperties.getDeliveryTag(), false);

        redisTemplate.opsForHash().delete(MQConstants.MQ_CONSUMER_RETRY_COUNT_KEY, messageProperties.getMessageId());
    }
}
