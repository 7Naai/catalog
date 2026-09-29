package com.pedidos360.catalog.publisher;

import com.pedidos360.catalog.config.RabbitMqConfig;
import com.pedidos360.catalog.model.dto.StockUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CatalogPublisher {

    private final RabbitTemplate rabbitTemplate;

    public CatalogPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishStockUpdated(String productId, Integer newStock) {
        StockUpdatedEvent event = new StockUpdatedEvent(productId, newStock);
        
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.EXCHANGE_CATALOG,
            RabbitMqConfig.ROUTING_KEY_STOCK_UPDATED,
            event
        );
    }
}