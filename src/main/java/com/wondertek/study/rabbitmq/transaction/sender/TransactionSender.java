package com.wondertek.study.rabbitmq.transaction.sender;

import com.wondertek.study.rabbitmq.transaction.annotation.TransMessage;
import com.wondertek.study.rabbitmq.transaction.util.DBCoordinator;
import com.wondertek.study.rabbitmq.transaction.util.MQConstants;
import com.wondertek.study.rabbitmq.transaction.util.RabbitMetaMessage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author zbc
 * @Date 1:49-2019/3/2
 */
@Component
@EnableAspectJAutoProxy
@Aspect
public class TransactionSender {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RabbitSender rabbitSender;

    @Autowired
    DBCoordinator dbCoordinator;

    @Autowired
    ApplicationContext applicationContext;

    @Pointcut("@annotation(com.wondertek.study.rabbitmq.transaction.annotation.TransMessage)")
    public void annotationSender() {

    }

    @Around("annotationSender()&&@annotation(rd)")
    public void sendMsg(ProceedingJoinPoint joinPoint, TransMessage rd) {
        logger.info("==> custom mq annotation,rd " + rd);
        String exchange = rd.exchange();
        String bindingKey = rd.bindingKey();
        String dc = rd.dbCoordinator();
        String bizName = rd.bizName() + MQConstants.DB_SPLIT + getCurrentDateTime();

        DBCoordinator dbCoordinator = null;
        try {
            dbCoordinator = (DBCoordinator) applicationContext.getBean("dbCoordinator");
        } catch (BeansException e) {
            logger.error("无消息存储类，事务执行终止");
            return;
        }

        //发送前暂存消息
        dbCoordinator.setMsgPrepare(bizName);

        Object returnObj = null;

        try {
            returnObj = joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error("业务执行失败,业务名称:" + bizName);
        }

        if(returnObj == null) {
            returnObj = MQConstants.BLANK_STR;
        }

        /** 生成一个发送对象 */
        RabbitMetaMessage rabbitMetaMessage = new RabbitMetaMessage();

        rabbitMetaMessage.setMessageId(bizName);
        /**设置交换机 */
        rabbitMetaMessage.setExchange(exchange);
        /**指定routing key */
        rabbitMetaMessage.setRoutingKey(bindingKey);
        /** 设置需要传递的消息体,可以是任意对象 */
        rabbitMetaMessage.setPayload(returnObj);

        /** 将消息设置为ready状态*/
        dbCoordinator.setMsgReady(bizName, rabbitMetaMessage);

        /** 发送消息 */
        try {
            rabbitSender.setCorrelationData(dc);
            rabbitSender.send(rabbitMetaMessage);
        } catch (Exception e) {
            logger.error("第一阶段消息发送异常" + bizName + e);
            throw e;
        }
    }

    public static String getCurrentDateTime(){
        SimpleDateFormat df = new SimpleDateFormat(MQConstants.TIME_PATTERN);
        return df.format(new Date());
    }
}
