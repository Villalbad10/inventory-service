package com.inventory_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario", nullable = false, unique = true)
    private Long idInventario;

    @NotNull(message = "El producto_id es obligatorio")
    @Column(name = "producto_id", nullable = false, unique = true)
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @PositiveOrZero(message = "La cantidad debe ser cero o positiva")
    @Column(nullable = false)
    private Integer cantidad;

    @Builder.Default
    @Column(name = "eliminado", nullable = false)
    private Boolean eliminado = false;

    /**
     * Timestamp de creación del registro.
     * Se establece automáticamente al crear el producto.
     */
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Timestamp de última modificación.
     * Se actualiza automáticamente al modificar el producto.
     */
    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}