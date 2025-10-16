package com.inventory_service.client.dto;

import lombok.Data;

/**
 * DTO que representa la respuesta del endpoint /products/{id} del product-service.
 */
@Data
public class ProductResponse {
    private Long idProducto;
    private String nombre;
    private Double precio;
    private String descripcion;
    private Boolean eliminado;
    private String fechaCreacion;
    private String fechaModificacion;
}


