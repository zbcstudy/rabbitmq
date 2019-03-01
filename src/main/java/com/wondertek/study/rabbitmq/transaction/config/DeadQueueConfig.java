package com.wondertek.study.rabbitmq.transaction.config;

import com.wondertek.study.rabbitmq.transaction.listener.DeadLetterMessageListener;
import com.wondertek.study.rabbitmq.transaction.util.MQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 配置死信队列
 * @Author zbc
 * @Date 23:03-2019/3/1
 */
@Component
public class DeadQueueConfig {

    /**
     * 死信交换机
     * @return
     */
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(MQConstants.DLX_EXCHANGE);
    }

    /**
     * 声明死信队列
     * @return
     */
    @Bean
    public Queue dlxQueue() {
        return new Queue(MQConstants.DLX_EXCHANGE, true, false, false);
    }

    /**
     * 声明死信路由绑定死信交换机和死信队列
     * @return
     */
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with(MQConstants.DLX_ROUTING_KEY);
    }

    @Bean
    public SimpleMessageListenerContainer deadLetterListenerContainer(ConnectionFactory connectionFactory,
                                                                      DeadLetterMessageListener deadLetterMessageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);

        container.setQueues(dlxQueue());
        container.setExposeListenerChannel(true);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(deadLetterMessageListener);

        //设置消息者能处理消息的最大个数
        container.setPrefetchCount(100);
        return container;
    }

}
