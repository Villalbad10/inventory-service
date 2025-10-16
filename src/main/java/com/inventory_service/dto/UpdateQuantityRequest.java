package com.inventory_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

/**
 * Request para actualizar la cantidad disponible de un producto en inventario.
 */
@Data
public class UpdateQuantityRequest {

    @Schema(description = "Nueva cantidad disponible (>= 0)", example = "25", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La cantidad es obligatoria")
    @PositiveOrZero(message = "La cantidad debe ser cero o positiva")
    private Integer cantidad;
}


