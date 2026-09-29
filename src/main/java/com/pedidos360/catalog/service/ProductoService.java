package com.pedidos360.catalog.service;

import com.pedidos360.catalog.model.Producto;
import com.pedidos360.catalog.model.dto.ProductoRequest;
import com.pedidos360.catalog.model.dto.ProductoResponse;
import com.pedidos360.catalog.publisher.CatalogPublisher; // 1. Importar el Publisher
import com.pedidos360.catalog.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CatalogPublisher catalogPublisher; 
    public ProductoService(ProductoRepository productoRepository, CatalogPublisher catalogPublisher) {
        this.productoRepository = productoRepository;
        this.catalogPublisher = catalogPublisher;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductos() {
        return productoRepository.findAll()
                .stream()
                .map(ProductoResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerProducto(String id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con id: " + id
                ));

        return new ProductoResponse(producto);
    }

    public ProductoResponse crearProducto(ProductoRequest request) {

        if (productoRepository.existsById(request.getId())) {
            throw new RuntimeException(
                    "Ya existe un producto con el id: " + request.getId()
            );
        }

        Producto producto = new Producto();

        producto.setId(request.getId());
        producto.setName(request.getName());
        producto.setPrice(request.getPrice());
        producto.setStock(request.getStock());

        Producto guardado = productoRepository.save(producto);
        catalogPublisher.publishStockUpdated(guardado.getId(), guardado.getStock());

        return new ProductoResponse(guardado);
    }

    public ProductoResponse actualizarProducto(String id, ProductoRequest request) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con id: " + id
                ));

        producto.setName(request.getName());
        producto.setPrice(request.getPrice());
        producto.setStock(request.getStock());
        Producto actualizado = productoRepository.save(producto);
        catalogPublisher.publishStockUpdated(actualizado.getId(), actualizado.getStock());
        return new ProductoResponse(actualizado);
    }
    public void eliminarProducto(String id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException(
                    "Producto no encontrado con id: " + id
            );
        }
        productoRepository.deleteById(id);
    }
    public ProductoResponse actualizarStock(String id, Integer stock) {
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException(
                    "El stock no puede ser negativo"
            );
        }
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con id: " + id
                ));
        producto.setStock(stock);
        Producto actualizado = productoRepository.save(producto);
        catalogPublisher.publishStockUpdated(actualizado.getId(), actualizado.getStock());
        return new ProductoResponse(actualizado);
    }
}