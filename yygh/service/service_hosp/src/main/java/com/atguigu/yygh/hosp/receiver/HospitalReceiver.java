package com.atguigu.yygh.hosp.receiver;

import com.atguigu.common.constant.MqConst;
import com.atguigu.common.service.RabbitService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * MQ消息监听器 - 处理订单状态变更后的号源更新
 * 使用 MongoDB 原子操作 $inc/$set 避免并发竞态条件
 */
@Component
public class HospitalReceiver {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_ORDER, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_ORDER),
            key = {MqConst.ROUTING_ORDER}
    ))
    public void receiver(OrderMqVo orderMqVo, Message message, Channel channel) throws IOException {
        if (null != orderMqVo.getAvailableNumber()) {
            // 下单成功：使用 MongoDB $set 原子操作直接更新号源数量，避免读-改-写竞态
            Query query = new Query(Criteria.where("_id").is(orderMqVo.getScheduleId()));
            Update update = new Update();
            update.set("reservedNumber", orderMqVo.getReservedNumber());
            update.set("availableNumber", orderMqVo.getAvailableNumber());
            mongoTemplate.updateFirst(query, update, Schedule.class);
        } else {
            // 取消预约：使用 MongoDB $inc 原子操作对剩余号源 +1，避免并发竞态条件
            Query query = new Query(Criteria.where("_id").is(orderMqVo.getScheduleId()));
            Update update = new Update();
            update.inc("availableNumber", 1);
            mongoTemplate.updateFirst(query, update, Schedule.class);
        }
        // 发送短信通知
        MsmVo msmVo = orderMqVo.getMsmVo();
        if (null != msmVo) {
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
        }
    }
}
