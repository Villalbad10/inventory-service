package com.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta de una compra exitosa.
 * Contiene toda la información relacionada con la compra realizada,
 * incluyendo detalles del producto, cantidades y totales.
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyResponse {

    /**
     * ID del producto comprado.
     */
    private Long productId;

    /**
     * Nombre del producto.
     */
    private String productName;

    /**
     * Cantidad de unidades compradas.
     */
    private Integer quantityPurchased;

    /**
     * Cantidad disponible restante en inventario después de la compra.
     */
    private Integer remainingQuantity;

    /**
     * Precio unitario del producto al momento de la compra.
     */
    private Double unitPrice;

    /**
     * Total de la compra calculado como precio unitario * cantidad comprada.
     */
    private Double totalAmount;

    /**
     * Fecha y hora exacta cuando se realizó la compra.
     */
    private LocalDateTime buyDate;

    /**
     * Mensaje de confirmación.
     */
    private String message;
}
