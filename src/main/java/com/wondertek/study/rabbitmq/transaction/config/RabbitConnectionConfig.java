package com.wondertek.study.rabbitmq.transaction.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq连接工厂配置
 * @Author zbc
 * @Date 1:02-2019/3/2
 */
@Configuration
@EnableRabbit
public class RabbitConnectionConfig {
    private Logger logger = LoggerFactory.getLogger(RabbitConnectionConfig.class);

    //将配置提出来，方便apollo配置中心,或做灵活配置
    @Value("${spring.rabbitmq.host}")
    String host;

    @Value("${spring.rabbitmq.port}")
    int port;

    @Value("${spring.rabbitmq.username}")
    String username;

    @Value("${spring.rabbitmq.password}")
    String password;

//    @Value("${spring.rabbitmq.connection-timeout}")
//    int connectionTimeout;
//
//    @Value("${spring.rabbitmq.template.receive-timeout}")
//    int receiveTimeout;

    @Value("${spring.rabbitmq.virtual.host}")
    String virtualHost;

    @Value("${spring.rabbitmq.cache.channel.size}")
    int cacheSize;

    /**
     * 自定义创建rabbitmq创建工厂
     */
    @Bean
    public CachingConnectionFactory rabbitConnectionFactory() {
        logger.info("==> custom rabbit connection factory");

        RabbitConnectionFactoryBean rabbitConnectionFactoryBean = new RabbitConnectionFactoryBean();
        rabbitConnectionFactoryBean.setHost(host);
        rabbitConnectionFactoryBean.setPort(port);

        rabbitConnectionFactoryBean.setUsername(username);
        rabbitConnectionFactoryBean.setPassword(password);
        rabbitConnectionFactoryBean.setVirtualHost(virtualHost);
//        rabbitConnectionFactoryBean.setConnectionTimeout(connectionTimeout);
//        rabbitConnectionFactoryBean.setAutomaticRecoveryEnabled(true);
        try {
            rabbitConnectionFactoryBean.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setPublisherConfirms(true);
        cachingConnectionFactory.setPublisherReturns(true);
        cachingConnectionFactory.setChannelCacheSize(cacheSize);
        return cachingConnectionFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        logger.info("==>custom rabbit listener factory:{}", connectionFactory);
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(connectionFactory);
        containerFactory.setConcurrentConsumers(3);
        containerFactory.setMaxConcurrentConsumers(10);
        containerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return containerFactory;
    }
}
