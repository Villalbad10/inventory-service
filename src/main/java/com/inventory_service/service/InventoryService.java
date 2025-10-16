package com.inventory_service.service;

import com.inventory_service.client.dto.ProductResponse;

/**
 * Servicio de dominio para operaciones de inventario.
 */
public interface InventoryService {

    /**
     * Obtiene la cantidad disponible en inventario para un producto.
     *
     * @param productId identificador del producto
     * @return cantidad disponible (no negativa)
     * @throws com.inventory_service.exception.NotFoundException si el producto no existe o no tiene inventario
     */
    Integer getAvailableQuantityByProductId(Long productId);

    /**
     * Obtiene el producto desde product-service por ID.
     *
     * @param productId identificador del producto
     * @return representación del producto tal como la expone product-service
     */
    ProductResponse getProductById(Long productId);

    /**
     * Actualiza (o crea) la cantidad disponible de inventario para un producto.
     * Valida la existencia del producto en product-service.
     *
     * @param productId identificador del producto
     * @param cantidad  nueva cantidad disponible (>= 0)
     * @return cantidad actualizada
     */
    Integer updateAvailableQuantity(Long productId, Integer cantidad);
}


