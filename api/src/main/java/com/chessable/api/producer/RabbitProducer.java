package com.chessable.api.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Service;

import com.chessable.library.entity.Account;
import com.chessable.api.config.RabbitConfig;
import com.chessable.library.entity.AccountGame;

@Service
public class RabbitProducer {
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private final Jackson2JsonMessageConverter converter;

    public RabbitProducer(RabbitTemplate rabbitTemplate, Jackson2JsonMessageConverter converter) {
        this.rabbitTemplate = rabbitTemplate;
        this.converter = converter;
    }


    public void sendAccount(Account account) {
        rabbitTemplate.convertAndSend("accountExchange", "routing.key.account", account);
    }

    public void sendUserTactic(Account message) {
        rabbitTemplate.convertAndSend(RabbitConfig.USER_TACTIC_EXCHANGE, RabbitConfig.USER_TACTIC_ROUTING_KEY, message);
    }

    public void sendUserMate(AccountGame message) {
        rabbitTemplate.convertAndSend(RabbitConfig.USER_MATE_EXCHANGE, RabbitConfig.USER_MATE_ROUTING_KEY, message);
    }
}
