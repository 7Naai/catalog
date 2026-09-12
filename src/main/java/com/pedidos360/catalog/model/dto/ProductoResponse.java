package com.pedidos360.catalog.model.dto;

import com.pedidos360.catalog.model.Producto;

public class ProductoResponse {

    private String id;
    private String name;
    private Double price;
    private Integer stock;

    public ProductoResponse() {
    }

    public ProductoResponse(Producto producto) {
        this.id = producto.getId();
        this.name = producto.getName();
        this.price = producto.getPrice();
        this.stock = producto.getStock();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}