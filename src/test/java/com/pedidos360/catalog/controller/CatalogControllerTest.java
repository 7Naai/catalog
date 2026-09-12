package com.pedidos360.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.pedidos360.catalog.model.dto.ProductoRequest;
import com.pedidos360.catalog.model.dto.ProductoResponse;
import com.pedidos360.catalog.service.ProductoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogController.class)
@AutoConfigureMockMvc(addFilters = false)
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductoService productoService;

    @Test
    void listarProductos_debeRetornar200() throws Exception {

        ProductoResponse producto = new ProductoResponse();

        producto.setId("PROD-001");
        producto.setName("Notebook Lenovo");
        producto.setPrice(599990.0);
        producto.setStock(10);

        when(productoService.listarProductos())
                .thenReturn(List.of(producto));

        mockMvc.perform(
                        get("/api/catalog/productos")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("PROD-001"))
                .andExpect(jsonPath("$[0].name").value("Notebook Lenovo"))
                .andExpect(jsonPath("$[0].price").value(599990.0))
                .andExpect(jsonPath("$[0].stock").value(10));

        verify(productoService, times(1))
                .listarProductos();
    }

    @Test
    void obtenerProducto_debeRetornar200() throws Exception {

        ProductoResponse producto = new ProductoResponse();

        producto.setId("PROD-001");
        producto.setName("Notebook Lenovo");
        producto.setPrice(599990.0);
        producto.setStock(10);

        when(productoService.obtenerProducto("PROD-001"))
                .thenReturn(producto);

        mockMvc.perform(
                        get("/api/catalog/productos/PROD-001")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PROD-001"))
                .andExpect(jsonPath("$.name")
                        .value("Notebook Lenovo"))
                .andExpect(jsonPath("$.stock").value(10));

        verify(productoService, times(1))
                .obtenerProducto("PROD-001");
    }

    @Test
    void crearProducto_debeRetornar201() throws Exception {

        ProductoRequest request = crearRequest();

        ProductoResponse response = new ProductoResponse();

        response.setId("PROD-001");
        response.setName("Notebook Lenovo");
        response.setPrice(599990.0);
        response.setStock(10);

        when(productoService.crearProducto(any(ProductoRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/catalog/productos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("PROD-001"))
                .andExpect(jsonPath("$.name")
                        .value("Notebook Lenovo"))
                .andExpect(jsonPath("$.price")
                        .value(599990.0))
                .andExpect(jsonPath("$.stock").value(10));

        verify(productoService, times(1))
                .crearProducto(any(ProductoRequest.class));
    }

    @Test
    void actualizarProducto_debeRetornar200() throws Exception {

        ProductoRequest request = crearRequest();

        ProductoResponse response = new ProductoResponse();

        response.setId("PROD-001");
        response.setName("Notebook Lenovo Actualizada");
        response.setPrice(599990.0);
        response.setStock(15);

        when(productoService.actualizarProducto(
                eq("PROD-001"),
                any(ProductoRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/catalog/productos/PROD-001")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PROD-001"))
                .andExpect(jsonPath("$.name")
                        .value("Notebook Lenovo Actualizada"))
                .andExpect(jsonPath("$.stock").value(15));

        verify(productoService, times(1))
                .actualizarProducto(
                        eq("PROD-001"),
                        any(ProductoRequest.class));
    }

    @Test
    void eliminarProducto_debeRetornar204() throws Exception {

        doNothing()
                .when(productoService)
                .eliminarProducto("PROD-001");

        mockMvc.perform(
                        delete("/api/catalog/productos/PROD-001")
                )
                .andExpect(status().isNoContent());

        verify(productoService, times(1))
                .eliminarProducto("PROD-001");
    }

    @Test
    void actualizarStock_debeRetornar200() throws Exception {

        ProductoResponse response = new ProductoResponse();

        response.setId("PROD-001");
        response.setName("Notebook Lenovo");
        response.setPrice(599990.0);
        response.setStock(20);

        when(productoService.actualizarStock("PROD-001", 20))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/api/catalog/productos/PROD-001/stock")
                                .param("stock", "20")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PROD-001"))
                .andExpect(jsonPath("$.stock").value(20));

        verify(productoService, times(1))
                .actualizarStock("PROD-001", 20);
    }

    @Test
    void crearProducto_sinNombre_debeRetornar400() throws Exception {

        ProductoRequest request = crearRequest();

        request.setName("");

        mockMvc.perform(
                        post("/api/catalog/productos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(productoService, never())
                .crearProducto(any(ProductoRequest.class));
    }

    private ProductoRequest crearRequest() {

        ProductoRequest request = new ProductoRequest();

        request.setId("PROD-001");
        request.setName("Notebook Lenovo");
        request.setPrice(599990.0);
        request.setStock(10);

        return request;
    }
}