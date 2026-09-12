package com.pedidos360.catalog.controller;

import com.pedidos360.catalog.model.dto.ProductoRequest;
import com.pedidos360.catalog.model.dto.ProductoResponse;
import com.pedidos360.catalog.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@Tag(name = "Catálogo", description = "Gestión de productos, stock y precios")
public class CatalogController {

    private final ProductoService productoService;

    public CatalogController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/productos")
    @Operation(summary = "Listar productos")
    public ResponseEntity<List<ProductoResponse>> listarProductos() {
        return ResponseEntity.ok(
                productoService.listarProductos()
        );
    }

    @GetMapping("/productos/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<ProductoResponse> obtenerProducto(
            @PathVariable String id) {

        return ResponseEntity.ok(
                productoService.obtenerProducto(id)
        );
    }

    @PostMapping("/productos")
    @Operation(summary = "Crear producto")
    public ResponseEntity<ProductoResponse> crearProducto(
            @Valid @RequestBody ProductoRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crearProducto(request));
    }

    @PutMapping("/productos/{id}")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<ProductoResponse> actualizarProducto(
            @PathVariable String id,
            @Valid @RequestBody ProductoRequest request) {

        return ResponseEntity.ok(
                productoService.actualizarProducto(id, request)
        );
    }

    @DeleteMapping("/productos/{id}")
    @Operation(summary = "Eliminar producto")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable String id) {

        productoService.eliminarProducto(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/productos/{id}/stock")
    @Operation(summary = "Actualizar stock")
    public ResponseEntity<ProductoResponse> actualizarStock(
            @PathVariable String id,
            @RequestParam Integer stock) {

        return ResponseEntity.ok(
                productoService.actualizarStock(id, stock)
        );
    }
}