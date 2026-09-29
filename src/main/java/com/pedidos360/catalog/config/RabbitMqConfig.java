package com.pedidos360.catalog.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_CATALOG = "catalog.direct.exchange";
    public static final String QUEUE_STOCK_UPDATED = "catalog.stock.updated.queue";
    public static final String ROUTING_KEY_STOCK_UPDATED = "catalog.stock.updated.key";

    @Bean
    public DirectExchange catalogExchange() {
        return new DirectExchange(EXCHANGE_CATALOG);
    }

    @Bean
    public Queue stockQueue() {
        return QueueBuilder.durable(QUEUE_STOCK_UPDATED).build();
    }

    @Bean
    public Binding stockBinding(Queue stockQueue, DirectExchange catalogExchange) {
        return BindingBuilder.bind(stockQueue).to(catalogExchange).with(ROUTING_KEY_STOCK_UPDATED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}