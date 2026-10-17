package com.pedidos360.catalog.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_CATALOG = "catalog.direct.exchange";
    public static final String QUEUE_STOCK_UPDATED = "catalog.stock.updated.queue";
    public static final String ROUTING_KEY_STOCK_UPDATED = "catalog.stock.updated.key";

    public static final String STOCK_CMD_QUEUE = "catalog.stock.cmd.queue";
    public static final String STOCK_CMD_ROUTING_KEY = "catalog.stock.cmd.key";

    public static final String DLX_EXCHANGE = "catalog.dlx.exchange";
    public static final String DLQ_QUEUE = "catalog.stock.dlq";
    public static final String DLQ_ROUTING_KEY = "catalog.dlk.key";

    @Bean
    public DirectExchange catalogExchange() {
        return new DirectExchange(EXCHANGE_CATALOG);
    }

    @Bean
    public Queue stockQueue() {
        return QueueBuilder.durable(QUEUE_STOCK_UPDATED).build();
    }

    @Bean
    public Binding stockBinding(@Qualifier("stockQueue") Queue stockQueue, 
                                @Qualifier("catalogExchange") DirectExchange catalogExchange) {
        return BindingBuilder.bind(stockQueue).to(catalogExchange).with(ROUTING_KEY_STOCK_UPDATED);
    }

    @Bean
    public Queue stockCmdQueue() {
        return QueueBuilder.durable(STOCK_CMD_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding stockCmdBinding(@Qualifier("stockCmdQueue") Queue stockCmdQueue, 
                                   @Qualifier("catalogExchange") DirectExchange catalogExchange) {
        return BindingBuilder.bind(stockCmdQueue).to(catalogExchange).with(STOCK_CMD_ROUTING_KEY);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding deadLetterBinding(@Qualifier("deadLetterQueue") Queue deadLetterQueue,
                                     @Qualifier("deadLetterExchange") DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DLQ_ROUTING_KEY);
    }

    // 5. Convertidor de mensajes JSON
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}