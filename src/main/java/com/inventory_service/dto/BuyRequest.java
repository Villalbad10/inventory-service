package com.inventory_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar la compra de un producto.
 * Contiene el ID del producto y la cantidad a comprar.
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyRequest {

    /**
     * ID del producto a comprar.
     */
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    /**
     * Cantidad de unidades a comprar.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer quantity;
}
