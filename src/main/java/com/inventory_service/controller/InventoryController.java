package com.inventory_service.controller;

import com.inventory_service.client.dto.ProductResponse;
import com.inventory_service.dto.BuyRequest;
import com.inventory_service.dto.BuyResponse;
import com.inventory_service.dto.UpdateQuantityRequest;
import com.inventory_service.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

/**
 * Controlador REST para operaciones de inventario.
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Controller", description = "Gestión de productos")
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Retorna la cantidad disponible en inventario para un producto específico.
     *
     * @param productId ID del producto
     * @return cantidad disponible
     */
    @Operation(summary = "Consulta la cantidad disponible de un producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cantidad disponible",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "404", description = "Producto o inventario no encontrado")
    })
    @GetMapping("/{productId}/available")
    public ResponseEntity<Integer> getAvailable(
            @Parameter(description = "Identificador del producto", required = true)
            @PathVariable Long productId) {
        Integer qty = inventoryService.getAvailableQuantityByProductId(productId);
        return ResponseEntity.ok(qty);
    }

    /**
     * Retorna el detalle de producto exactamente como lo expone product-service.
     */
    @Operation(summary = "Obtiene el detalle del producto desde product-service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "Identificador del producto", required = true)
            @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getProductById(productId));
    }

    /**
     * Actualiza la cantidad disponible en inventario para un producto (upsert).
     */
    @Operation(summary = "Actualiza la cantidad disponible de un producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cantidad actualizada",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/update/{productId}")
    public ResponseEntity<Integer> updateAvailable(
            @Parameter(description = "Identificador del producto", required = true)
            @PathVariable Long productId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        Integer updated = inventoryService.updateAvailableQuantity(productId, request.getCantidad());
        return ResponseEntity.ok(updated);
    }

    /**
     * Procesa una compra de producto.
     * Permite a los clientes comprar productos verificando la disponibilidad
     * en inventario y actualizando las cantidades disponibles tras la compra.
     * Retorna información detallada de la transacción realizada.
     */
    @Operation(summary = "Procesa una compra de producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compra realizada exitosamente",
                    content = @Content(schema = @Schema(implementation = BuyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Inventario insuficiente para realizar la compra solicitada"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado o eliminado")
    })
    @PostMapping("/buy")
    public ResponseEntity<BuyResponse> buyProduct(
            @Valid @RequestBody BuyRequest request) {
        BuyResponse response = inventoryService.buyProduct(request);
        return ResponseEntity.ok(response);
    }
}


