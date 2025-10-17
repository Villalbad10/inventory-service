package com.inventory_service.repository;

import com.inventory_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio para la entidad Inventory.
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    /**
     * Busca un inventario por el ID del producto y que no esté eliminado.
     * @param productoId ID del producto
     * @return Optional con el inventario encontrado
     */
    Optional<Inventory> findByProductoIdAndEliminadoFalse(Long productoId);
}


