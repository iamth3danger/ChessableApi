package com.chessable.worker.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.chessable.worker.config.RabbitConfig;
import com.chessable.library.message.MateReq;
import com.chessable.library.message.StockMessage;
import com.chessable.library.entity.Account;
import com.chessable.library.entity.AccountGame;

public class RabbitProducer {


    private final RabbitTemplate rabbitTemplate;

    public RabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMateReq(MateReq message) {
        rabbitTemplate.convertAndSend(RabbitConfig.MATE_EXCHANGE, RabbitConfig.MATE_ROUTING_KEY, message);
    }
    
    public void sendTacticInstances(StockMessage message) {
        rabbitTemplate.convertAndSend(RabbitConfig.TACTIC_EXCHANGE, RabbitConfig.TACTIC_ROUTING_KEY, message);
    }

    public void sendUserTactic(Account message) {
        rabbitTemplate.convertAndSend(RabbitConfig.USER_TACTIC_EXCHANGE, RabbitConfig.USER_TACTIC_ROUTING_KEY, message);
    }

    public void sendUserMate(AccountGame message) {
        rabbitTemplate.convertAndSend(RabbitConfig.USER_MATE_EXCHANGE, RabbitConfig.USER_MATE_ROUTING_KEY, message);
    }
}
