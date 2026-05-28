package com.atguigu.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ 消息发送服务
 */
@Slf4j
@Service
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到指定交换机
     *
     * @param exchange   交换机名称
     * @param routingKey 路由键
     * @param message    消息体
     * @return true 发送成功，false 发送失败
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            return true;
        } catch (Exception e) {
            log.error("RabbitMQ消息发送失败, exchange={}, routingKey={}", exchange, routingKey, e);
            return false;
        }
    }
}
