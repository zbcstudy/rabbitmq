package com.wondertek.study.rabbitmq.transaction.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author zbc
 * @Date 0:07-2019/3/2
 */
@Configuration
public class RabbitTempleteConfig {
    private Logger log = LoggerFactory.getLogger(RabbitTempleteConfig.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    boolean returnFlag = false;

    @Bean
    public RabbitTemplate customRabbitTemplate(ConnectionFactory connectionFactory) {
        log.info("==> custom rabbitTemplete, connectionFactory:{}" + connectionFactory);

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        //mandatory必须设置为true，ReturnCallBack才会调用
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (returnFlag) {
                    log.error("mq发送错误，无对应的交换机");
                }
            }
        });

        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
