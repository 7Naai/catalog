package com.pedidos360.catalog.listener;

import com.pedidos360.catalog.config.RabbitMqConfig;
import com.pedidos360.catalog.model.dto.StockUpdateCommand;
import com.pedidos360.catalog.model.dto.ProductoResponse;
import com.pedidos360.catalog.service.ProductoService;
import com.pedidos360.catalog.publisher.CatalogPublisher;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CatalogListener {

    private final ProductoService productoService;
    private final CatalogPublisher catalogPublisher;

    public CatalogListener(ProductoService productoService, CatalogPublisher catalogPublisher) {
        this.productoService = productoService;
        this.catalogPublisher = catalogPublisher;
    }

    @RabbitListener(queues = RabbitMqConfig.STOCK_CMD_QUEUE)
    public void handleStockUpdateCommand(
            StockUpdateCommand command,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        try {
            System.out.println("Procesando comando de stock para producto: " + command.getProductId());

            ProductoResponse producto = productoService.obtenerProducto(command.getProductId());

            int nuevoStock = Math.max(0, producto.getStock() - command.getQuantityToDeduct());

            productoService.actualizarStock(command.getProductId(), nuevoStock);

            catalogPublisher.publishStockUpdated(command.getProductId(), nuevoStock);

            System.out.println("Stock actualizado exitosamente por RabbitMQ para producto: " + command.getProductId());

            channel.basicAck(tag, false);

        } catch (Exception e) {
            System.err.println("Error procesando comando de stock. Reorientando a DLQ: " + e.getMessage());

            channel.basicNack(tag, false, false);
        }
    }
}