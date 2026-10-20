package com.chessable.stockfish.stockfish.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.chessable.stockfish.stockfish.config.RabbitConfig;
import com.chessable.library.message.MateRes;

public class RabbitProducer {

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public RabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMateRes(MateRes mateRes) {
        rabbitTemplate.convertAndSend(RabbitConfig.MATE_RESPONSE_EXCHANGE, RabbitConfig.MATE_RESPONSE_ROUTING_KEY, mateRes);
    }
}
