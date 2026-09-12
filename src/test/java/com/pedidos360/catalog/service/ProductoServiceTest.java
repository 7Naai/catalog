package com.pedidos360.catalog.service;

import com.pedidos360.catalog.model.Producto;
import com.pedidos360.catalog.model.dto.ProductoRequest;
import com.pedidos360.catalog.model.dto.ProductoResponse;
import com.pedidos360.catalog.repository.ProductoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private ProductoRequest request;

    @BeforeEach
    void setUp() {

        producto = new Producto(
                "PROD-001",
                "Notebook Lenovo",
                599990.0,
                10
        );

        request = new ProductoRequest();

        request.setId("PROD-001");
        request.setName("Notebook Lenovo");
        request.setPrice(599990.0);
        request.setStock(10);
    }

    @Test
    void listarProductos_debeRetornarProductos() {

        when(productoRepository.findAll())
                .thenReturn(List.of(producto));

        List<ProductoResponse> resultado =
                productoService.listarProductos();

        assertEquals(1, resultado.size());
        assertEquals("PROD-001", resultado.get(0).getId());
        assertEquals("Notebook Lenovo", resultado.get(0).getName());
        assertEquals(599990.0, resultado.get(0).getPrice());
        assertEquals(10, resultado.get(0).getStock());

        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void obtenerProducto_debeRetornarProducto() {

        when(productoRepository.findById("PROD-001"))
                .thenReturn(Optional.of(producto));

        ProductoResponse resultado =
                productoService.obtenerProducto("PROD-001");

        assertNotNull(resultado);
        assertEquals("PROD-001", resultado.getId());
        assertEquals("Notebook Lenovo", resultado.getName());

        verify(productoRepository, times(1))
                .findById("PROD-001");
    }

    @Test
    void obtenerProducto_productoNoExiste_debeLanzarExcepcion() {

        when(productoRepository.findById("PROD-999"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productoService.obtenerProducto("PROD-999")
        );

        assertEquals(
                "Producto no encontrado con id: PROD-999",
                exception.getMessage()
        );
    }

    @Test
    void crearProducto_debeGuardarProducto() {

        when(productoRepository.existsById("PROD-001"))
                .thenReturn(false);

        when(productoRepository.save(any(Producto.class)))
                .thenReturn(producto);

        ProductoResponse resultado =
                productoService.crearProducto(request);

        assertNotNull(resultado);
        assertEquals("PROD-001", resultado.getId());
        assertEquals("Notebook Lenovo", resultado.getName());
        assertEquals(599990.0, resultado.getPrice());
        assertEquals(10, resultado.getStock());

        verify(productoRepository, times(1))
                .existsById("PROD-001");

        verify(productoRepository, times(1))
                .save(any(Producto.class));
    }

    @Test
    void crearProducto_idExistente_debeLanzarExcepcion() {

        when(productoRepository.existsById("PROD-001"))
                .thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productoService.crearProducto(request)
        );

        assertEquals(
                "Ya existe un producto con el id: PROD-001",
                exception.getMessage()
        );

        verify(productoRepository, never())
                .save(any(Producto.class));
    }

    @Test
    void actualizarProducto_debeActualizarProducto() {

        when(productoRepository.findById("PROD-001"))
                .thenReturn(Optional.of(producto));

        when(productoRepository.save(any(Producto.class)))
                .thenReturn(producto);

        request.setName("Notebook Lenovo Actualizada");
        request.setStock(15);

        ProductoResponse resultado =
                productoService.actualizarProducto(
                        "PROD-001",
                        request
                );

        assertNotNull(resultado);

        assertEquals(
                "Notebook Lenovo Actualizada",
                producto.getName()
        );

        assertEquals(15, producto.getStock());

        verify(productoRepository, times(1))
                .findById("PROD-001");

        verify(productoRepository, times(1))
                .save(any(Producto.class));
    }

    @Test
    void eliminarProducto_debeEliminarProducto() {

        when(productoRepository.existsById("PROD-001"))
                .thenReturn(true);

        productoService.eliminarProducto("PROD-001");

        verify(productoRepository, times(1))
                .existsById("PROD-001");

        verify(productoRepository, times(1))
                .deleteById("PROD-001");
    }

    @Test
    void eliminarProducto_productoNoExiste_debeLanzarExcepcion() {

        when(productoRepository.existsById("PROD-999"))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productoService.eliminarProducto("PROD-999")
        );

        assertEquals(
                "Producto no encontrado con id: PROD-999",
                exception.getMessage()
        );

        verify(productoRepository, never())
                .deleteById("PROD-999");
    }

    @Test
    void actualizarStock_debeActualizarStock() {

        when(productoRepository.findById("PROD-001"))
                .thenReturn(Optional.of(producto));

        when(productoRepository.save(any(Producto.class)))
                .thenReturn(producto);

        ProductoResponse resultado =
                productoService.actualizarStock(
                        "PROD-001",
                        20
                );

        assertNotNull(resultado);
        assertEquals(20, producto.getStock());

        verify(productoRepository, times(1))
                .save(producto);
    }

    @Test
    void actualizarStock_stockNegativo_debeLanzarExcepcion() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productoService.actualizarStock(
                        "PROD-001",
                        -5
                )
        );

        assertEquals(
                "El stock no puede ser negativo",
                exception.getMessage()
        );

        verify(productoRepository, never())
                .save(any(Producto.class));
    }
}