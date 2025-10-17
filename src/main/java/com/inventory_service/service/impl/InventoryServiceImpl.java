package com.inventory_service.service.impl;

import com.inventory_service.client.ProductClient;
import com.inventory_service.client.dto.ProductResponse;
import com.inventory_service.dto.BuyRequest;
import com.inventory_service.dto.BuyResponse;
import com.inventory_service.exception.BadRequestException;
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

import java.time.LocalDateTime;

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
     * Actualiza la cantidad disponible de un producto.
     * @param productId ID del producto
     * @param cantidad nueva cantidad disponible
     * @return cantidad actualizada
     * @throws NotFoundException si el producto no existe
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
     * Obtiene el detalle del producto desde product-service por ID.
     * @param productId ID del producto
     * @return detalle del producto
     * @throws NotFoundException si el producto no existe
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

    /**
     * Implementación del método para procesar una compra de producto.
     * Realiza las siguientes operaciones de forma transaccional:
     * 1. Valida la existencia del producto en product-service
     * 2. Verifica la disponibilidad en inventario
     * 3. Actualiza las cantidades disponibles
     * 4. Calcula totales y genera respuesta detallada
     */
    @Override
    @Transactional
    @Operation(summary = "Procesa una compra de producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compra realizada exitosamente",
                    content = @Content(schema = @Schema(implementation = BuyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Inventario insuficiente para realizar la compra"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado o eliminado")
    })
    public BuyResponse buyProduct(BuyRequest request) {
        Long productId = request.getProductId();
        Integer quantityToPurchase = request.getQuantity();

        // Validar existencia del producto
        ProductResponse product = productClient.getProductById(productId);
        if (product == null || product.getIdProducto() == null || Boolean.TRUE.equals(product.getEliminado())) {
            throw new NotFoundException("Producto no encontrado en product-service");
        }

        // Obtener inventario actual
        Inventory inventory = inventoryRepository.findByProductoIdAndEliminadoFalse(productId)
                .orElseThrow(() -> new NotFoundException("Inventario no encontrado para el producto"));

        // Verificar disponibilidad
        Integer currentQuantity = inventory.getCantidad();
        if (currentQuantity < quantityToPurchase) {
            throw new BadRequestException(
                    String.format("Inventario insuficiente. Disponible: %d, Solicitado: %d", 
                            currentQuantity, quantityToPurchase)
            );
        }

        // Actualizar inventario
        Integer newQuantity = currentQuantity - quantityToPurchase;
        inventory.setCantidad(newQuantity);
        inventoryRepository.save(inventory);

        // Calcular total de la compra
        Double unitPrice = product.getPrecio() != null ? product.getPrecio() : 0.0;
        Double totalAmount = unitPrice * quantityToPurchase;

        // Construir respuesta
        return BuyResponse.builder()
                .productId(productId)
                .productName(product.getNombre())
                .quantityPurchased(quantityToPurchase)
                .remainingQuantity(newQuantity)
                .unitPrice(unitPrice)
                .totalAmount(totalAmount)
                .buyDate(LocalDateTime.now())
                .message(String.format("Compra exitosa de %d unidades de %s", quantityToPurchase, product.getNombre()))
                .build();
    }
}


