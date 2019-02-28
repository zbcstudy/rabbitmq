package com.wondertek.study.rabbitmq.transaction.util;

import org.springframework.amqp.rabbit.support.CorrelationData;

/**
 * @Author zbc
 * @Date 23:01-2019/2/28
 */
public class CompleteCorrelationData extends CorrelationData {
    private String coordinator;

    public CompleteCorrelationData(String id, String coordinator){
        super(id);
        this.coordinator = coordinator;
    }

    public String getCoordinator(){
        return this.coordinator;
    }

    @Override
    public String toString(){
        return "CompleteCorrelationData id=" + getId() +",coordinator" + this.coordinator;
    }
}
