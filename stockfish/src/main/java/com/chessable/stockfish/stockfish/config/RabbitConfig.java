package com.chessable.stockfish.stockfish.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String TACTIC_QUEUE = "tactic.queue";
    public static final String TACTIC_EXCHANGE = "tactic.exchange";
    public static final String TACTIC_ROUTING_KEY = "routing.key.tactic";


    public static final String TACTIC_VERIFICATION_QUEUE = "tactic.verification.queue";
    public static final String TACTIC_VERIFICATION_EXCHANGE = "tactic.verification.exchange";
    public static final String TACTIC_VERIFICATION_ROUTING_KEY = "routing.key.tactic.verification";

    public static final String MATE_QUEUE = "mate.queue";
    public static final String MATE_EXCHANGE = "mate.exchange";
    public static final String MATE_ROUTING_KEY = "routing.key.mate";

    public static final String MATE_RESPONSE_QUEUE = "mate.response.queue";
    public static final String MATE_RESPONSE_EXCHANGE = "mate.response.exchange";
    public static final String MATE_RESPONSE_ROUTING_KEY = "routing.key.mate.response";

    @Bean
    public Queue tacticQueue() {
        return new Queue(TACTIC_QUEUE, true); // durable queue
    }

    @Bean
    public Queue tacticVerificationQueue() {
        return new Queue(TACTIC_VERIFICATION_QUEUE, true); // durable queue
    }
}
