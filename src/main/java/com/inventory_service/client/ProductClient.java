package com.inventory_service.client;

import com.inventory_service.config.ProductFeignConfig;
import com.inventory_service.client.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para el servicio de productos.
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@FeignClient(
        name = "product-service",
        url = "${products.service.url}",
        configuration = ProductFeignConfig.class
)
public interface ProductClient {

    @GetMapping("/products/{id}")
    ProductResponse getProductById(@PathVariable("id") Long id);
}


