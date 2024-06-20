package com.aruba.Lib.service;

import com.aruba.Lib.logging.logger.MsLogger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {


    @Value("${rabbitmq.outboundExchange}")
    private String exchange;

    @Value("${rabbitmq.outboundRoutingKey}")
    private String routingKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(Object body){
        MsLogger.logger.info("Sending message to:{} rk:{}",exchange,routingKey );
        rabbitTemplate.convertAndSend(exchange,routingKey,body);
    }


}
