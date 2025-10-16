package com.inventory_service.repository;

import com.inventory_service.model.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para InventoryRepository.
 * 
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("InventoryRepository Integration Tests")
class InventoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InventoryRepository inventoryRepository;

    private Inventory validInventory;
    private Inventory deletedInventory;
    private Inventory anotherValidInventory;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada prueba
        inventoryRepository.deleteAll();

        // Configurar inventario válido
        validInventory = Inventory.builder()
                .productoId(1L)
                .cantidad(10)
                .eliminado(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();

        // Configurar inventario eliminado
        deletedInventory = Inventory.builder()
                .productoId(2L)
                .cantidad(5)
                .eliminado(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();

        // Configurar otro inventario válido
        anotherValidInventory = Inventory.builder()
                .productoId(3L)
                .cantidad(20)
                .eliminado(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("findByProductoIdAndEliminadoFalse - Debe encontrar inventario cuando existe y no está eliminado")
    void findByProductoIdAndEliminadoFalse_WhenInventoryExistsAndNotDeleted_ShouldReturnInventory() {
        // Given
        entityManager.persistAndFlush(validInventory);
        Long productId = validInventory.getProductoId();

        // When
        Optional<Inventory> result = inventoryRepository.findByProductoIdAndEliminadoFalse(productId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(validInventory.getIdInventario(), result.get().getIdInventario());
        assertEquals(validInventory.getProductoId(), result.get().getProductoId());
        assertEquals(validInventory.getCantidad(), result.get().getCantidad());
        assertFalse(result.get().getEliminado());
    }

    @Test
    @DisplayName("findByProductoIdAndEliminadoFalse - Debe retornar empty cuando inventario no existe")
    void findByProductoIdAndEliminadoFalse_WhenInventoryNotExists_ShouldReturnEmpty() {
        // Given
        Long nonExistentProductId = 999L;

        // When
        Optional<Inventory> result = inventoryRepository.findByProductoIdAndEliminadoFalse(nonExistentProductId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByProductoIdAndEliminadoFalse - Debe retornar empty cuando inventario está eliminado")
    void findByProductoIdAndEliminadoFalse_WhenInventoryIsDeleted_ShouldReturnEmpty() {
        // Given
        entityManager.persistAndFlush(deletedInventory);
        Long productId = deletedInventory.getProductoId();

        // When
        Optional<Inventory> result = inventoryRepository.findByProductoIdAndEliminadoFalse(productId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByProductoIdAndEliminadoFalse - Debe encontrar el inventario correcto entre múltiples")
    void findByProductoIdAndEliminadoFalse_WithMultipleInventories_ShouldReturnCorrectOne() {
        // Given
        entityManager.persistAndFlush(validInventory);
        entityManager.persistAndFlush(deletedInventory);
        entityManager.persistAndFlush(anotherValidInventory);

        // When
        Optional<Inventory> result1 = inventoryRepository.findByProductoIdAndEliminadoFalse(validInventory.getProductoId());
        Optional<Inventory> result2 = inventoryRepository.findByProductoIdAndEliminadoFalse(deletedInventory.getProductoId());
        Optional<Inventory> result3 = inventoryRepository.findByProductoIdAndEliminadoFalse(anotherValidInventory.getProductoId());

        // Then
        assertTrue(result1.isPresent());
        assertEquals(validInventory.getProductoId(), result1.get().getProductoId());
        assertFalse(result1.get().getEliminado());

        assertFalse(result2.isPresent()); // Deleted inventory should not be found

        assertTrue(result3.isPresent());
        assertEquals(anotherValidInventory.getProductoId(), result3.get().getProductoId());
        assertFalse(result3.get().getEliminado());
    }

    @Test
    @DisplayName("save - Debe guardar inventario correctamente")
    void save_ShouldSaveInventoryCorrectly() {
        // Given
        Inventory newInventory = Inventory.builder()
                .productoId(4L)
                .cantidad(15)
                .eliminado(false)
                .build();

        // When
        Inventory savedInventory = inventoryRepository.save(newInventory);
        entityManager.flush();

        // Then
        assertNotNull(savedInventory.getIdInventario());
        assertEquals(newInventory.getProductoId(), savedInventory.getProductoId());
        assertEquals(newInventory.getCantidad(), savedInventory.getCantidad());
        assertEquals(newInventory.getEliminado(), savedInventory.getEliminado());
        assertNotNull(savedInventory.getFechaCreacion());
        assertNotNull(savedInventory.getFechaModificacion());
    }

    @Test
    @DisplayName("save - Debe actualizar inventario existente")
    void save_ShouldUpdateExistingInventory() {
        // Given
        entityManager.persistAndFlush(validInventory);
        Long inventoryId = validInventory.getIdInventario();

        // When
        validInventory.setCantidad(25);
        validInventory.setEliminado(true);
        Inventory updatedInventory = inventoryRepository.save(validInventory);
        entityManager.flush();

        // Then
        assertEquals(inventoryId, updatedInventory.getIdInventario());
        assertEquals(25, updatedInventory.getCantidad());
        assertTrue(updatedInventory.getEliminado());
        assertNotNull(updatedInventory.getFechaModificacion());
    }

    @Test
    @DisplayName("findAll - Debe retornar todos los inventarios")
    void findAll_ShouldReturnAllInventories() {
        // Given
        entityManager.persistAndFlush(validInventory);
        entityManager.persistAndFlush(deletedInventory);
        entityManager.persistAndFlush(anotherValidInventory);

        // When
        List<Inventory> allInventories = inventoryRepository.findAll();

        // Then
        assertEquals(3, allInventories.size());
        assertTrue(allInventories.stream().anyMatch(inv -> inv.getProductoId().equals(validInventory.getProductoId())));
        assertTrue(allInventories.stream().anyMatch(inv -> inv.getProductoId().equals(deletedInventory.getProductoId())));
        assertTrue(allInventories.stream().anyMatch(inv -> inv.getProductoId().equals(anotherValidInventory.getProductoId())));
    }

    @Test
    @DisplayName("findById - Debe encontrar inventario por ID")
    void findById_ShouldFindInventoryById() {
        // Given
        entityManager.persistAndFlush(validInventory);
        Long inventoryId = validInventory.getIdInventario();

        // When
        Optional<Inventory> result = inventoryRepository.findById(inventoryId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(validInventory.getIdInventario(), result.get().getIdInventario());
        assertEquals(validInventory.getProductoId(), result.get().getProductoId());
        assertEquals(validInventory.getCantidad(), result.get().getCantidad());
    }

    @Test
    @DisplayName("findById - Debe retornar empty cuando ID no existe")
    void findById_WhenIdNotExists_ShouldReturnEmpty() {
        // Given
        Long nonExistentId = 999L;

        // When
        Optional<Inventory> result = inventoryRepository.findById(nonExistentId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("delete - Debe eliminar inventario")
    void delete_ShouldDeleteInventory() {
        // Given
        entityManager.persistAndFlush(validInventory);
        Long inventoryId = validInventory.getIdInventario();

        // When
        inventoryRepository.delete(validInventory);
        entityManager.flush();

        // Then
        Optional<Inventory> result = inventoryRepository.findById(inventoryId);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("deleteById - Debe eliminar inventario por ID")
    void deleteById_ShouldDeleteInventoryById() {
        // Given
        entityManager.persistAndFlush(validInventory);
        Long inventoryId = validInventory.getIdInventario();

        // When
        inventoryRepository.deleteById(inventoryId);
        entityManager.flush();

        // Then
        Optional<Inventory> result = inventoryRepository.findById(inventoryId);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("count - Debe contar inventarios correctamente")
    void count_ShouldCountInventoriesCorrectly() {
        // Given
        assertEquals(0, inventoryRepository.count());

        entityManager.persistAndFlush(validInventory);
        assertEquals(1, inventoryRepository.count());

        entityManager.persistAndFlush(deletedInventory);
        assertEquals(2, inventoryRepository.count());

        entityManager.persistAndFlush(anotherValidInventory);
        assertEquals(3, inventoryRepository.count());
    }

    @Test
    @DisplayName("existsById - Debe verificar existencia por ID")
    void existsById_ShouldCheckExistenceById() {
        // Given
        entityManager.persistAndFlush(validInventory);
        Long inventoryId = validInventory.getIdInventario();
        Long nonExistentId = 999L;

        // When & Then
        assertTrue(inventoryRepository.existsById(inventoryId));
        assertFalse(inventoryRepository.existsById(nonExistentId));
    }

    @Test
    @DisplayName("findByProductoIdAndEliminadoFalse - Debe manejar productoId nulo")
    void findByProductoIdAndEliminadoFalse_WithNullProductId_ShouldHandleCorrectly() {
        // When
        Optional<Inventory> result = inventoryRepository.findByProductoIdAndEliminadoFalse(null);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByProductoIdAndEliminadoFalse - Debe manejar productoId cero")
    void findByProductoIdAndEliminadoFalse_WithZeroProductId_ShouldHandleCorrectly() {
        // Given
        Long zeroProductId = 0L;

        // When
        Optional<Inventory> result = inventoryRepository.findByProductoIdAndEliminadoFalse(zeroProductId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByProductoIdAndEliminadoFalse - Debe manejar productoId negativo")
    void findByProductoIdAndEliminadoFalse_WithNegativeProductId_ShouldHandleCorrectly() {
        // Given
        Long negativeProductId = -1L;

        // When
        Optional<Inventory> result = inventoryRepository.findByProductoIdAndEliminadoFalse(negativeProductId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("save - Debe manejar inventario con cantidad cero")
    void save_WithZeroQuantity_ShouldHandleCorrectly() {
        // Given
        Inventory inventoryWithZeroQuantity = Inventory.builder()
                .productoId(5L)
                .cantidad(0)
                .eliminado(false)
                .build();

        // When
        Inventory savedInventory = inventoryRepository.save(inventoryWithZeroQuantity);
        entityManager.flush();

        // Then
        assertNotNull(savedInventory.getIdInventario());
        assertEquals(0, savedInventory.getCantidad());
        assertFalse(savedInventory.getEliminado());
    }

    @Test
    @DisplayName("save - Debe manejar inventario con cantidad negativa")
    void save_WithNegativeQuantity_ShouldHandleCorrectly() {
        // Given
        Inventory inventoryWithNegativeQuantity = Inventory.builder()
                .productoId(6L)
                .cantidad(-5)
                .eliminado(false)
                .build();

        // When & Then
        // This should fail validation, so we expect an exception
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> {
            inventoryRepository.save(inventoryWithNegativeQuantity);
            entityManager.flush();
        });
    }
}
