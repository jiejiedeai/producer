package com.mq.producer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Description 消息发送确认
 * <p>
 * ConfirmCallback  只确认消息是否正确到达 Exchange 中
 * ReturnCallback   消息没有正确到达队列时触发回调，如果正确到达队列不执行
 * <p>
 * 1. 如果消息没有到exchange,则confirm回调,ack=false
 * 2. 如果消息到达exchange,则confirm回调,ack=true
 * 3. exchange到queue成功,则不回调return
 * 4. exchange到queue失败,则回调return
 */
@Component
@Slf4j
public class ProducerAckConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);            //指定 ConfirmCallback
        rabbitTemplate.setReturnCallback(this);             //指定 ReturnCallback

    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息发送成功" + correlationData);
            //如果引入可靠性投递机制 需要更新数据库
        } else {
            log.info("消息发送失败:" + cause);
            Message returnedMessage = correlationData.getReturnedMessage();
            //重发消息次数
            rabbitTemplate.convertAndSend("test1",returnedMessage,correlationData);
            log.info("重试发送消息:"+new String(returnedMessage.getBody()));

        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        // 反序列化对象输出
        log.info("消息主体: " + new String(message.getBody()));
        log.info("应答码: " + replyCode);
        log.info("描述：" + replyText);
        log.info("消息使用的交换器 exchange : " + exchange);
        log.info("消息使用的路由键 routing : " + routingKey);
    }
}
