package com.chessable.api.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    private static final String ACCOUNT_QUEUE = "accountQueue";
    private static final String ACCOUNT_EXCHANGE = "accountExchange";
    private static final String ACCOUNT_ROUTING_KEY = "routing.key.account";

    public static final String USER_TACTIC_QUEUE = "user.tactic.queue";
    public static final String USER_TACTIC_EXCHANGE = "user.tactic.exchange";
    public static final String USER_TACTIC_ROUTING_KEY = "routing.key.user.tactic";

    public static final String USER_MATE_QUEUE = "user.mate.queue";
    public static final String USER_MATE_EXCHANGE = "user.mate.exchange";
    public static final String USER_MATE_ROUTING_KEY = "routing.key.user.mate";

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(ACCOUNT_EXCHANGE);
    }

    @Bean
    public DirectExchange userTacticExchange() {
        return new DirectExchange(USER_TACTIC_EXCHANGE);
    }
    @Bean
    public DirectExchange userMateExchange() {
        return new DirectExchange(USER_MATE_EXCHANGE);
    }

    @Bean
    public Queue accountQueue() {
        return new Queue(ACCOUNT_QUEUE, true);
    }
    @Bean
    public Queue userTacticQueue() {
        return new Queue(USER_TACTIC_QUEUE, true);
    }
    @Bean
    public Queue userMateQueue() {
        return new Queue(USER_MATE_QUEUE, true);
    }

    @Bean
    public Binding accountBinding(Queue accountQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(accountQueue)
                             .to(directExchange)
                             .with(ACCOUNT_ROUTING_KEY);
    }
    @Bean
    public Binding userTacticBinding(Queue userTacticQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(userTacticQueue)
                             .to(directExchange)
                             .with(USER_TACTIC_ROUTING_KEY);
    }
    @Bean
    public Binding userMateBinding(Queue userMateQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(userMateQueue)
                             .to(directExchange)
                             .with(USER_MATE_ROUTING_KEY);
    }
}
