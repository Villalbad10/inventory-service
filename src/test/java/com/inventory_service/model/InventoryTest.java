package com.inventory_service.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad Inventory.
 * 
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@DisplayName("Inventory Entity Tests")
class InventoryTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Inventory - Debe crear inventario válido con builder")
    void inventory_WithValidData_ShouldCreateValidInventory() {
        // Given
        Long productoId = 1L;
        Integer cantidad = 10;
        Boolean eliminado = false;

        // When
        Inventory inventory = Inventory.builder()
                .productoId(productoId)
                .cantidad(cantidad)
                .eliminado(eliminado)
                .build();

        // Then
        assertNotNull(inventory);
        assertEquals(productoId, inventory.getProductoId());
        assertEquals(cantidad, inventory.getCantidad());
        assertEquals(eliminado, inventory.getEliminado());
        assertNull(inventory.getIdInventario()); // No se estableció en el builder
        assertNull(inventory.getFechaCreacion()); // No se estableció en el builder
        assertNull(inventory.getFechaModificacion()); // No se estableció en el builder
    }

    @Test
    @DisplayName("Inventory - Debe crear inventario con constructor por defecto")
    void inventory_WithDefaultConstructor_ShouldCreateEmptyInventory() {
        // When
        Inventory inventory = new Inventory();

        // Then
        assertNotNull(inventory);
        assertNull(inventory.getIdInventario());
        assertNull(inventory.getProductoId());
        assertNull(inventory.getCantidad());
        assertFalse(inventory.getEliminado());
        assertNull(inventory.getFechaCreacion());
        assertNull(inventory.getFechaModificacion());
    }

    @Test
    @DisplayName("Inventory - Debe crear inventario con constructor con argumentos")
    void inventory_WithArgsConstructor_ShouldCreateInventoryWithValues() {
        // Given
        Long idInventario = 1L;
        Long productoId = 2L;
        Integer cantidad = 15;
        Boolean eliminado = false;
        LocalDateTime fechaCreacion = LocalDateTime.now();
        LocalDateTime fechaModificacion = LocalDateTime.now();

        // When
        Inventory inventory = new Inventory(idInventario, productoId, cantidad, eliminado, fechaCreacion, fechaModificacion);

        // Then
        assertNotNull(inventory);
        assertEquals(idInventario, inventory.getIdInventario());
        assertEquals(productoId, inventory.getProductoId());
        assertEquals(cantidad, inventory.getCantidad());
        assertEquals(eliminado, inventory.getEliminado());
        assertEquals(fechaCreacion, inventory.getFechaCreacion());
        assertEquals(fechaModificacion, inventory.getFechaModificacion());
    }

    @Test
    @DisplayName("Inventory - Debe usar setters y getters correctamente")
    void inventory_WithSettersAndGetters_ShouldWorkCorrectly() {
        // Given
        Inventory inventory = new Inventory();
        Long idInventario = 3L;
        Long productoId = 4L;
        Integer cantidad = 20;
        Boolean eliminado = true;
        LocalDateTime fechaCreacion = LocalDateTime.now().minusDays(1);
        LocalDateTime fechaModificacion = LocalDateTime.now();

        // When
        inventory.setIdInventario(idInventario);
        inventory.setProductoId(productoId);
        inventory.setCantidad(cantidad);
        inventory.setEliminado(eliminado);
        inventory.setFechaCreacion(fechaCreacion);
        inventory.setFechaModificacion(fechaModificacion);

        // Then
        assertEquals(idInventario, inventory.getIdInventario());
        assertEquals(productoId, inventory.getProductoId());
        assertEquals(cantidad, inventory.getCantidad());
        assertEquals(eliminado, inventory.getEliminado());
        assertEquals(fechaCreacion, inventory.getFechaCreacion());
        assertEquals(fechaModificacion, inventory.getFechaModificacion());
    }

    @Test
    @DisplayName("Inventory - Debe validar productoId nulo")
    void inventory_WithNullProductoId_ShouldHaveValidationError() {
        // Given
        Inventory inventory = Inventory.builder()
                .productoId(null)
                .cantidad(10)
                .eliminado(false)
                .build();

        // When
        Set<ConstraintViolation<Inventory>> violations = validator.validate(inventory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("productoId")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("obligatorio")));
    }

    @Test
    @DisplayName("Inventory - Debe validar cantidad nula")
    void inventory_WithNullCantidad_ShouldHaveValidationError() {
        // Given
        Inventory inventory = Inventory.builder()
                .productoId(1L)
                .cantidad(null)
                .eliminado(false)
                .build();

        // When
        Set<ConstraintViolation<Inventory>> violations = validator.validate(inventory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cantidad")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("obligatoria")));
    }

    @Test
    @DisplayName("Inventory - Debe validar cantidad negativa")
    void inventory_WithNegativeCantidad_ShouldHaveValidationError() {
        // Given
        Inventory inventory = Inventory.builder()
                .productoId(1L)
                .cantidad(-1)
                .eliminado(false)
                .build();

        // When
        Set<ConstraintViolation<Inventory>> violations = validator.validate(inventory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cantidad")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("cero o positiva")));
    }

    @Test
    @DisplayName("Inventory - Debe aceptar cantidad cero")
    void inventory_WithZeroCantidad_ShouldBeValid() {
        // Given
        Inventory inventory = Inventory.builder()
                .productoId(1L)
                .cantidad(0)
                .eliminado(false)
                .build();

        // When
        Set<ConstraintViolation<Inventory>> violations = validator.validate(inventory);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Inventory - Debe aceptar cantidad positiva")
    void inventory_WithPositiveCantidad_ShouldBeValid() {
        // Given
        Inventory inventory = Inventory.builder()
                .productoId(1L)
                .cantidad(100)
                .eliminado(false)
                .build();

        // When
        Set<ConstraintViolation<Inventory>> violations = validator.validate(inventory);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Inventory - Debe establecer eliminado como false por defecto")
    void inventory_WithDefaultEliminado_ShouldBeFalse() {
        // Given
        Inventory inventory = Inventory.builder()
                .productoId(1L)
                .cantidad(10)
                .build(); // No se establece eliminado

        // When
        Boolean eliminado = inventory.getEliminado();

        // Then
        assertFalse(eliminado);
    }

    @Test
    @DisplayName("Inventory - Debe establecer eliminado como true cuando se especifica")
    void inventory_WithEliminadoTrue_ShouldBeTrue() {
        // Given
        Inventory inventory = Inventory.builder()
                .productoId(1L)
                .cantidad(10)
                .eliminado(true)
                .build();

        // When
        Boolean eliminado = inventory.getEliminado();

        // Then
        assertTrue(eliminado);
    }

    @Test
    @DisplayName("Inventory - Debe manejar fechas correctamente")
    void inventory_WithDates_ShouldHandleCorrectly() {
        // Given
        LocalDateTime fechaCreacion = LocalDateTime.now().minusDays(1);
        LocalDateTime fechaModificacion = LocalDateTime.now();

        Inventory inventory = Inventory.builder()
                .productoId(1L)
                .cantidad(10)
                .eliminado(false)
                .fechaCreacion(fechaCreacion)
                .fechaModificacion(fechaModificacion)
                .build();

        // When & Then
        assertEquals(fechaCreacion, inventory.getFechaCreacion());
        assertEquals(fechaModificacion, inventory.getFechaModificacion());
    }

    @Test
    @DisplayName("Inventory - Debe manejar fechas nulas")
    void inventory_WithNullDates_ShouldHandleCorrectly() {
        // Given
        Inventory inventory = Inventory.builder()
                .productoId(1L)
                .cantidad(10)
                .eliminado(false)
                .fechaCreacion(null)
                .fechaModificacion(null)
                .build();

        // When & Then
        assertNull(inventory.getFechaCreacion());
        assertNull(inventory.getFechaModificacion());
    }

    @Test
    @DisplayName("Inventory - Debe usar toString correctamente")
    void inventory_ToString_ShouldWorkCorrectly() {
        // Given
        Inventory inventory = Inventory.builder()
                .idInventario(1L)
                .productoId(2L)
                .cantidad(10)
                .eliminado(false)
                .build();

        // When
        String toString = inventory.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Inventory"));
        assertTrue(toString.contains("idInventario=1"));
        assertTrue(toString.contains("productoId=2"));
        assertTrue(toString.contains("cantidad=10"));
        assertTrue(toString.contains("eliminado=false"));
    }

    @Test
    @DisplayName("Inventory - Debe usar equals correctamente")
    void inventory_Equals_ShouldWorkCorrectly() {
        // Given
        Inventory inventory1 = Inventory.builder()
                .idInventario(1L)
                .productoId(2L)
                .cantidad(10)
                .eliminado(false)
                .build();

        Inventory inventory2 = Inventory.builder()
                .idInventario(1L)
                .productoId(2L)
                .cantidad(10)
                .eliminado(false)
                .build();

        Inventory inventory3 = Inventory.builder()
                .idInventario(2L)
                .productoId(2L)
                .cantidad(10)
                .eliminado(false)
                .build();

        // When & Then
        assertEquals(inventory1, inventory2);
        assertNotEquals(inventory1, inventory3);
        assertNotEquals(inventory1, null);
        assertNotEquals(inventory1, "string");
    }

    @Test
    @DisplayName("Inventory - Debe usar hashCode correctamente")
    void inventory_HashCode_ShouldWorkCorrectly() {
        // Given
        Inventory inventory1 = Inventory.builder()
                .idInventario(1L)
                .productoId(2L)
                .cantidad(10)
                .eliminado(false)
                .build();

        Inventory inventory2 = Inventory.builder()
                .idInventario(1L)
                .productoId(2L)
                .cantidad(10)
                .eliminado(false)
                .build();

        Inventory inventory3 = Inventory.builder()
                .idInventario(2L)
                .productoId(2L)
                .cantidad(10)
                .eliminado(false)
                .build();

        // When & Then
        assertEquals(inventory1.hashCode(), inventory2.hashCode());
        assertNotEquals(inventory1.hashCode(), inventory3.hashCode());
    }

    @Test
    @DisplayName("Inventory - Debe manejar valores extremos")
    void inventory_WithExtremeValues_ShouldHandleCorrectly() {
        // Given
        Long maxProductoId = Long.MAX_VALUE;
        Integer maxCantidad = Integer.MAX_VALUE;
        Integer minCantidad = 0;

        // When
        Inventory inventoryMax = Inventory.builder()
                .productoId(maxProductoId)
                .cantidad(maxCantidad)
                .eliminado(false)
                .build();

        Inventory inventoryMin = Inventory.builder()
                .productoId(1L)
                .cantidad(minCantidad)
                .eliminado(false)
                .build();

        // Then
        assertEquals(maxProductoId, inventoryMax.getProductoId());
        assertEquals(maxCantidad, inventoryMax.getCantidad());
        assertEquals(minCantidad, inventoryMin.getCantidad());
    }
}
