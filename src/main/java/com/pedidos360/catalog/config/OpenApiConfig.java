package com.pedidos360.catalog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI catalogOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Pedidos360 - Catálogo API")
                        .description(
                                "API REST para la gestión de productos, "
                                        + "stock y precios del sistema Pedidos360."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pedidos360")));
    }
}