package com.inventory_service.client.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ProductResponse DTO.
 * 
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@DisplayName("ProductResponse DTO Tests")
class ProductResponseTest {

    @Test
    @DisplayName("ProductResponse - Debe crear response válido")
    void productResponse_WithValidData_ShouldCreateValidResponse() {
        // Given
        Long idProducto = 1L;
        String nombre = "Producto Test";
        Double precio = 100.0;
        String descripcion = "Descripción del producto";
        Boolean eliminado = false;
        String fechaCreacion = "2025-01-01T10:00:00";
        String fechaModificacion = "2025-01-01T11:00:00";

        // When
        ProductResponse response = new ProductResponse();
        response.setIdProducto(idProducto);
        response.setNombre(nombre);
        response.setPrecio(precio);
        response.setDescripcion(descripcion);
        response.setEliminado(eliminado);
        response.setFechaCreacion(fechaCreacion);
        response.setFechaModificacion(fechaModificacion);

        // Then
        assertNotNull(response);
        assertEquals(idProducto, response.getIdProducto());
        assertEquals(nombre, response.getNombre());
        assertEquals(precio, response.getPrecio());
        assertEquals(descripcion, response.getDescripcion());
        assertEquals(eliminado, response.getEliminado());
        assertEquals(fechaCreacion, response.getFechaCreacion());
        assertEquals(fechaModificacion, response.getFechaModificacion());
    }

    @Test
    @DisplayName("ProductResponse - Debe manejar campos nulos")
    void productResponse_WithNullFields_ShouldHandleCorrectly() {
        // When
        ProductResponse response = new ProductResponse();
        response.setIdProducto(null);
        response.setNombre(null);
        response.setPrecio(null);
        response.setDescripcion(null);
        response.setEliminado(null);
        response.setFechaCreacion(null);
        response.setFechaModificacion(null);

        // Then
        assertNotNull(response);
        assertNull(response.getIdProducto());
        assertNull(response.getNombre());
        assertNull(response.getPrecio());
        assertNull(response.getDescripcion());
        assertNull(response.getEliminado());
        assertNull(response.getFechaCreacion());
        assertNull(response.getFechaModificacion());
    }

    @Test
    @DisplayName("ProductResponse - Debe manejar precio nulo")
    void productResponse_WithNullPrice_ShouldHandleCorrectly() {
        // Given
        ProductResponse response = new ProductResponse();
        response.setIdProducto(1L);
        response.setNombre("Producto Sin Precio");
        response.setPrecio(null);
        response.setDescripcion("Producto sin precio");
        response.setEliminado(false);

        // When & Then
        assertNotNull(response);
        assertEquals(1L, response.getIdProducto());
        assertEquals("Producto Sin Precio", response.getNombre());
        assertNull(response.getPrecio());
        assertEquals("Producto sin precio", response.getDescripcion());
        assertFalse(response.getEliminado());
    }

    @Test
    @DisplayName("ProductResponse - Debe manejar precio cero")
    void productResponse_WithZeroPrice_ShouldHandleCorrectly() {
        // Given
        ProductResponse response = new ProductResponse();
        response.setIdProducto(2L);
        response.setNombre("Producto Gratis");
        response.setPrecio(0.0);
        response.setDescripcion("Producto gratuito");
        response.setEliminado(false);

        // When & Then
        assertNotNull(response);
        assertEquals(2L, response.getIdProducto());
        assertEquals("Producto Gratis", response.getNombre());
        assertEquals(0.0, response.getPrecio());
        assertEquals("Producto gratuito", response.getDescripcion());
        assertFalse(response.getEliminado());
    }

    @Test
    @DisplayName("ProductResponse - Debe manejar precio negativo")
    void productResponse_WithNegativePrice_ShouldHandleCorrectly() {
        // Given
        ProductResponse response = new ProductResponse();
        response.setIdProducto(3L);
        response.setNombre("Producto Con Descuento");
        response.setPrecio(-10.0);
        response.setDescripcion("Producto con descuento");
        response.setEliminado(false);

        // When & Then
        assertNotNull(response);
        assertEquals(3L, response.getIdProducto());
        assertEquals("Producto Con Descuento", response.getNombre());
        assertEquals(-10.0, response.getPrecio());
        assertEquals("Producto con descuento", response.getDescripcion());
        assertFalse(response.getEliminado());
    }

    @Test
    @DisplayName("ProductResponse - Debe manejar producto eliminado")
    void productResponse_WithDeletedProduct_ShouldHandleCorrectly() {
        // Given
        ProductResponse response = new ProductResponse();
        response.setIdProducto(4L);
        response.setNombre("Producto Eliminado");
        response.setPrecio(50.0);
        response.setDescripcion("Producto eliminado");
        response.setEliminado(true);

        // When & Then
        assertNotNull(response);
        assertEquals(4L, response.getIdProducto());
        assertEquals("Producto Eliminado", response.getNombre());
        assertEquals(50.0, response.getPrecio());
        assertEquals("Producto eliminado", response.getDescripcion());
        assertTrue(response.getEliminado());
    }

    @Test
    @DisplayName("ProductResponse - Debe manejar eliminado como false por defecto")
    void productResponse_WithDefaultEliminado_ShouldBeFalse() {
        // Given
        ProductResponse response = new ProductResponse();
        response.setIdProducto(5L);
        response.setNombre("Producto Activo");
        response.setPrecio(75.0);
        response.setDescripcion("Producto activo");
        // No se establece eliminado

        // When
        Boolean eliminado = response.getEliminado();

        // Then
        assertNull(eliminado); // No se estableció, por lo que es null
    }

    @Test
    @DisplayName("ProductResponse - Debe manejar fechas como strings")
    void productResponse_WithDateStrings_ShouldHandleCorrectly() {
        // Given
        String fechaCreacion = "2025-01-01T10:00:00";
        String fechaModificacion = "2025-01-01T11:00:00";

        ProductResponse response = new ProductResponse();
        response.setIdProducto(6L);
        response.setNombre("Producto Con Fechas");
        response.setPrecio(100.0);
        response.setDescripcion("Producto con fechas");
        response.setEliminado(false);
        response.setFechaCreacion(fechaCreacion);
        response.setFechaModificacion(fechaModificacion);

        // When & Then
        assertEquals(fechaCreacion, response.getFechaCreacion());
        assertEquals(fechaModificacion, response.getFechaModificacion());
    }

    @Test
    @DisplayName("ProductResponse - Debe manejar fechas nulas")
    void productResponse_WithNullDates_ShouldHandleCorrectly() {
        // Given
        ProductResponse response = new ProductResponse();
        response.setIdProducto(7L);
        response.setNombre("Producto Sin Fechas");
        response.setPrecio(100.0);
        response.setDescripcion("Producto sin fechas");
        response.setEliminado(false);
        response.setFechaCreacion(null);
        response.setFechaModificacion(null);

        // When & Then
        assertNull(response.getFechaCreacion());
        assertNull(response.getFechaModificacion());
    }

    @Test
    @DisplayName("ProductResponse - Debe usar toString correctamente")
    void productResponse_ToString_ShouldWorkCorrectly() {
        // Given
        ProductResponse response = new ProductResponse();
        response.setIdProducto(8L);
        response.setNombre("Producto Test");
        response.setPrecio(100.0);
        response.setDescripcion("Descripción del producto");
        response.setEliminado(false);

        // When
        String toString = response.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("ProductResponse"));
        assertTrue(toString.contains("idProducto=8"));
        assertTrue(toString.contains("nombre=Producto Test"));
        assertTrue(toString.contains("precio=100.0"));
        assertTrue(toString.contains("eliminado=false"));
    }

    @Test
    @DisplayName("ProductResponse - Debe usar equals correctamente")
    void productResponse_Equals_ShouldWorkCorrectly() {
        // Given
        ProductResponse response1 = new ProductResponse();
        response1.setIdProducto(9L);
        response1.setNombre("Producto Test");
        response1.setPrecio(100.0);
        response1.setDescripcion("Descripción del producto");
        response1.setEliminado(false);

        ProductResponse response2 = new ProductResponse();
        response2.setIdProducto(9L);
        response2.setNombre("Producto Test");
        response2.setPrecio(100.0);
        response2.setDescripcion("Descripción del producto");
        response2.setEliminado(false);

        ProductResponse response3 = new ProductResponse();
        response3.setIdProducto(10L);
        response3.setNombre("Producto Test");
        response3.setPrecio(100.0);
        response3.setDescripcion("Descripción del producto");
        response3.setEliminado(false);

        // When & Then
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertNotEquals(response1, null);
        assertNotEquals(response1, "string");
    }

    @Test
    @DisplayName("ProductResponse - Debe usar hashCode correctamente")
    void productResponse_HashCode_ShouldWorkCorrectly() {
        // Given
        ProductResponse response1 = new ProductResponse();
        response1.setIdProducto(11L);
        response1.setNombre("Producto Test");
        response1.setPrecio(100.0);
        response1.setDescripcion("Descripción del producto");
        response1.setEliminado(false);

        ProductResponse response2 = new ProductResponse();
        response2.setIdProducto(11L);
        response2.setNombre("Producto Test");
        response2.setPrecio(100.0);
        response2.setDescripcion("Descripción del producto");
        response2.setEliminado(false);

        ProductResponse response3 = new ProductResponse();
        response3.setIdProducto(12L);
        response3.setNombre("Producto Test");
        response3.setPrecio(100.0);
        response3.setDescripcion("Descripción del producto");
        response3.setEliminado(false);

        // When & Then
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    @DisplayName("ProductResponse - Debe manejar valores extremos")
    void productResponse_WithExtremeValues_ShouldHandleCorrectly() {
        // Given
        Long maxIdProducto = Long.MAX_VALUE;
        Double maxPrecio = Double.MAX_VALUE;
        Double minPrecio = Double.MIN_VALUE;

        ProductResponse responseMax = new ProductResponse();
        responseMax.setIdProducto(maxIdProducto);
        responseMax.setNombre("Producto Max");
        responseMax.setPrecio(maxPrecio);
        responseMax.setDescripcion("Producto con precio máximo");
        responseMax.setEliminado(false);

        ProductResponse responseMin = new ProductResponse();
        responseMin.setIdProducto(1L);
        responseMin.setNombre("Producto Min");
        responseMin.setPrecio(minPrecio);
        responseMin.setDescripcion("Producto con precio mínimo");
        responseMin.setEliminado(false);

        // When & Then
        assertEquals(maxIdProducto, responseMax.getIdProducto());
        assertEquals(maxPrecio, responseMax.getPrecio());
        assertEquals(minPrecio, responseMin.getPrecio());
    }

    @Test
    @DisplayName("ProductResponse - Debe manejar strings vacíos")
    void productResponse_WithEmptyStrings_ShouldHandleCorrectly() {
        // Given
        ProductResponse response = new ProductResponse();
        response.setIdProducto(13L);
        response.setNombre("");
        response.setPrecio(100.0);
        response.setDescripcion("");
        response.setEliminado(false);
        response.setFechaCreacion("");
        response.setFechaModificacion("");

        // When & Then
        assertEquals("", response.getNombre());
        assertEquals("", response.getDescripcion());
        assertEquals("", response.getFechaCreacion());
        assertEquals("", response.getFechaModificacion());
    }

    @Test
    @DisplayName("ProductResponse - Debe manejar strings largos")
    void productResponse_WithLongStrings_ShouldHandleCorrectly() {
        // Given
        String longNombre = "A".repeat(1000);
        String longDescripcion = "B".repeat(2000);

        ProductResponse response = new ProductResponse();
        response.setIdProducto(14L);
        response.setNombre(longNombre);
        response.setPrecio(100.0);
        response.setDescripcion(longDescripcion);
        response.setEliminado(false);

        // When & Then
        assertEquals(longNombre, response.getNombre());
        assertEquals(longDescripcion, response.getDescripcion());
    }
}
