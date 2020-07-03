package com.mq.producer.controller;

import com.alibaba.fastjson.JSON;
import com.mq.model.entity.User;
import com.mq.producer.config.CustomerException;
import com.mq.producer.model.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/test/")
@Api(tags = "测试")
public class TestController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @ApiOperation(value = "生产者发送消息", notes = "生产者发送消息")
    @PostMapping("sendMesage")
    public JsonResult<String> sendMesage(User user) throws CustomerException {
        Message message =new Message(JSON.toJSONString(user).getBytes(),new MessageProperties());
        //实际消息的id
        CorrelationData data = new CorrelationData();
        data.setId(UUID.randomUUID().toString());
        data.setReturnedMessage(message);
        rabbitTemplate.convertAndSend("exchange-test1","test.mq",user,data);
        return JsonResult.success("消息发送成功");
    }

}
