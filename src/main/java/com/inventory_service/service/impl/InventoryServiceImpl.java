package com.inventory_service.service.impl;

import com.inventory_service.client.ProductClient;
import com.inventory_service.client.dto.ProductResponse;
import com.inventory_service.exception.NotFoundException;
import com.inventory_service.model.Inventory;
import com.inventory_service.repository.InventoryRepository;
import com.inventory_service.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de inventario.
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductClient productClient;

    
    /**
     * Obtiene la cantidad disponible de un producto por ID.
     * @param productId ID del producto
     * @return cantidad disponible
     * @throws NotFoundException si el producto o el inventario no existe
     */
    @Override
    @Operation(summary = "Obtiene la cantidad disponible de un producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cantidad disponible encontrada",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "404", description = "Producto o inventario no encontrado")
    })
    public Integer getAvailableQuantityByProductId(Long productId) {
        // Validar existencia del producto a través del product-service
        ProductResponse product = productClient.getProductById(productId);
        if (product == null || product.getIdProducto() == null || Boolean.TRUE.equals(product.getEliminado())) {
            throw new NotFoundException("Producto no encontrado en product-service");
        }

        Inventory inventory = inventoryRepository.findByProductoIdAndEliminadoFalse(productId)
                .orElseThrow(() -> new NotFoundException("Inventario no encontrado para el producto"));
        return inventory.getCantidad();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @Operation(summary = "Actualiza la cantidad disponible de un producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cantidad actualizada",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public Integer updateAvailableQuantity(Long productId, Integer cantidad) {
        ProductResponse product = productClient.getProductById(productId);
        if (product == null || product.getIdProducto() == null || Boolean.TRUE.equals(product.getEliminado())) {
            throw new NotFoundException("Producto no encontrado en product-service");
        }

        Inventory inventory = inventoryRepository.findByProductoIdAndEliminadoFalse(productId)
                .orElseGet(() -> Inventory.builder()
                        .productoId(productId)
                        .cantidad(0)
                        .eliminado(false)
                        .build());

        inventory.setCantidad(cantidad);
        Inventory saved = inventoryRepository.save(inventory);
        return saved.getCantidad();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Operation(summary = "Obtiene el detalle del producto desde product-service por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ProductResponse getProductById(Long productId) {
        ProductResponse product = productClient.getProductById(productId);
        if (product == null || product.getIdProducto() == null || Boolean.TRUE.equals(product.getEliminado())) {
            throw new NotFoundException("Producto no encontrado en product-service");
        }
        return product;
    }
}


