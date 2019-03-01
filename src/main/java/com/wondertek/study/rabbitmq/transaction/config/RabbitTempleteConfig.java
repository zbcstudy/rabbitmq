package com.wondertek.study.rabbitmq.transaction.config;

import com.wondertek.study.rabbitmq.transaction.util.CompleteCorrelationData;
import com.wondertek.study.rabbitmq.transaction.util.DBCoordinator;
import com.wondertek.study.rabbitmq.transaction.util.MQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

    @Autowired
    ApplicationContext applicationContext;

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
                    log.error("mq发送错误，无对应的交换机，confirm回掉,ack={},correlationData={} cause={} returnFlag={}",
                            ack, correlationData, cause, returnFlag);
                }

                log.info("confirm回调，ack={} correlationData={} cause={}", ack, correlationData, cause);

                String mgId = correlationData.getId();

                //只要消息能投入正确的消息队列,并持久化,就返回ack为true
                if (ack) {
                    log.info("消息已正确投递到队列, correlationData:{}", correlationData);
                    //消除重发缓存
                    String dbcoordinator = ((CompleteCorrelationData) correlationData).getCoordinator();

                    DBCoordinator dbCoordinator = (DBCoordinator) applicationContext.getBean(dbcoordinator);
                    dbCoordinator.setMsgSuccess(mgId);
                }else {
                    log.error("消息投递至交换机失败,业务号:{}，原因:{}",correlationData.getId(),cause);
                }

            }

        });

        //消息发送到RabbitMQ交换器，但无相应exchange时的回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                String messageId = message.getMessageProperties().getMessageId();
                log.error("return回调,没有找到任何匹配的队列！message id:{},replyCode:{},replyText:{}," +
                        "exchange:{},routingKey:{}", messageId, replyCode, replyText, exchange, routingKey);
                returnFlag = true;
            }
        });

        //confirm超时时间
        rabbitTemplate.setReplyTimeout(MQConstants.TIME_GAP);

        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
