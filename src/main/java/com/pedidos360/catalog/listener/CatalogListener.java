package com.pedidos360.catalog.listener;

import com.pedidos360.catalog.model.dto.StockUpdateCommand;
import com.pedidos360.catalog.model.dto.ProductoResponse;
import com.pedidos360.catalog.service.ProductoService;
import com.pedidos360.catalog.publisher.CatalogPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CatalogListener {

    private final ProductoService productoService;
    private final CatalogPublisher catalogPublisher;

    public CatalogListener(ProductoService productoService, CatalogPublisher catalogPublisher) {
        this.productoService = productoService;
        this.catalogPublisher = catalogPublisher;
    }

    @RabbitListener(queues = "catalog.stock.cmd.queue")
    public void handleStockUpdateCommand(StockUpdateCommand command) {
        try {
            ProductoResponse producto = productoService.obtenerProducto(command.getProductId());
            
            int nuevoStock = Math.max(0, producto.getStock() - command.getQuantityToDeduct());
            
            productoService.actualizarStock(command.getProductId(), nuevoStock);
                catalogPublisher.publishStockUpdated(command.getProductId(), nuevoStock);
            
            System.out.println("Stock actualizado por RabbitMQ para producto: " + command.getProductId());
            
        } catch (Exception e) {
            System.err.println("Error actualizando stock asincrónicamente: " + e.getMessage());
        }
    }
}