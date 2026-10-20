package com.chessable.worker.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    public static final String ACCOUNT_QUEUE = "accountQueue";
    public static final String ACCOUNT_EXCHANGE = "accountExchange";
    public static final String ACCOUNT_ROUTING_KEY = "routing.key.account";

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

    public static final String USER_TACTIC_QUEUE = "user.tactic.queue";
    public static final String USER_TACTIC_EXCHANGE = "user.tactic.exchange";
    public static final String USER_TACTIC_ROUTING_KEY = "routing.key.user.tactic";

    public static final String USER_MATE_QUEUE = "user.mate.queue";
    public static final String USER_MATE_EXCHANGE = "user.mate.exchange";
    public static final String USER_MATE_ROUTING_KEY = "routing.key.user.mate";


    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(5);     
        factory.setMaxConcurrentConsumers(10); 
        return factory;
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(ACCOUNT_EXCHANGE);
    }

    @Bean
    public DirectExchange mateResponseExchange() {
        return new DirectExchange(MATE_RESPONSE_EXCHANGE);
    }

    @Bean
    public DirectExchange mateExchange() {
        return new DirectExchange(MATE_EXCHANGE);
    }

    @Bean
    public Queue accountQueue() {
        return new Queue(ACCOUNT_QUEUE, true);
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
    public DirectExchange tacticVerificationExchange() {
        return new DirectExchange(TACTIC_VERIFICATION_EXCHANGE);
    }

    @Bean
    public DirectExchange tacticExchange() {
        return new DirectExchange(TACTIC_EXCHANGE);
    }

    @Bean
    public Queue tacticQueue() {
        return new Queue(TACTIC_QUEUE, true);
    }

    @Bean
    public Queue tacticVerificationQueue() {
        return new Queue(TACTIC_VERIFICATION_QUEUE, true);
    }

    @Bean
    public Queue mateQueue() {
        return new Queue(MATE_QUEUE, true);
    }

    @Bean
    public Queue mateResponseQueue() {
        return new Queue(MATE_RESPONSE_QUEUE, true);
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
    public Binding tacticBinding(Queue tacticQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(tacticQueue)
                             .to(directExchange)
                             .with(TACTIC_ROUTING_KEY);
    }

    @Bean
    public Binding tacticVerificationBinding(Queue tacticVerificationQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(tacticVerificationQueue)
                             .to(directExchange)
                             .with(TACTIC_VERIFICATION_ROUTING_KEY);
    }

    @Bean
    public Binding mateBinding(Queue mateQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(mateQueue)
                             .to(directExchange)
                             .with(MATE_ROUTING_KEY);
    }

    @Bean
    public Binding mateResponseBinding(Queue mateResponseQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(mateResponseQueue)
                             .to(directExchange)
                             .with(MATE_RESPONSE_ROUTING_KEY);
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
