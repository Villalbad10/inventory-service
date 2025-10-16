package com.inventory_service.client;

import com.inventory_service.client.dto.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductClient.
 * 
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductClient Tests")
class ProductClientTest {

    @Mock
    private ProductClient productClient;

    @Test
    @DisplayName("getProductById - Debe retornar producto cuando existe")
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Given
        Long productId = 1L;
        ProductResponse expectedProduct = new ProductResponse();
        expectedProduct.setIdProducto(productId);
        expectedProduct.setNombre("Producto Test");
        expectedProduct.setPrecio(100.0);
        expectedProduct.setDescripcion("Descripción del producto");
        expectedProduct.setEliminado(false);

        when(productClient.getProductById(productId)).thenReturn(expectedProduct);

        // When
        ProductResponse result = productClient.getProductById(productId);

        // Then
        assertNotNull(result);
        assertEquals(expectedProduct.getIdProducto(), result.getIdProducto());
        assertEquals(expectedProduct.getNombre(), result.getNombre());
        assertEquals(expectedProduct.getPrecio(), result.getPrecio());
        assertEquals(expectedProduct.getDescripcion(), result.getDescripcion());
        assertEquals(expectedProduct.getEliminado(), result.getEliminado());
        
        verify(productClient).getProductById(productId);
    }

    @Test
    @DisplayName("getProductById - Debe retornar null cuando producto no existe")
    void getProductById_WhenProductNotExists_ShouldReturnNull() {
        // Given
        Long productId = 999L;
        when(productClient.getProductById(productId)).thenReturn(null);

        // When
        ProductResponse result = productClient.getProductById(productId);

        // Then
        assertNull(result);
        verify(productClient).getProductById(productId);
    }

    @Test
    @DisplayName("getProductById - Debe retornar producto eliminado")
    void getProductById_WhenProductIsDeleted_ShouldReturnDeletedProduct() {
        // Given
        Long productId = 2L;
        ProductResponse deletedProduct = new ProductResponse();
        deletedProduct.setIdProducto(productId);
        deletedProduct.setNombre("Producto Eliminado");
        deletedProduct.setPrecio(50.0);
        deletedProduct.setDescripcion("Producto eliminado");
        deletedProduct.setEliminado(true);

        when(productClient.getProductById(productId)).thenReturn(deletedProduct);

        // When
        ProductResponse result = productClient.getProductById(productId);

        // Then
        assertNotNull(result);
        assertEquals(deletedProduct.getIdProducto(), result.getIdProducto());
        assertEquals(deletedProduct.getNombre(), result.getNombre());
        assertEquals(deletedProduct.getPrecio(), result.getPrecio());
        assertEquals(deletedProduct.getDescripcion(), result.getDescripcion());
        assertTrue(result.getEliminado());
        
        verify(productClient).getProductById(productId);
    }

    @Test
    @DisplayName("getProductById - Debe retornar producto con precio nulo")
    void getProductById_WhenProductHasNullPrice_ShouldReturnProductWithNullPrice() {
        // Given
        Long productId = 3L;
        ProductResponse productWithNullPrice = new ProductResponse();
        productWithNullPrice.setIdProducto(productId);
        productWithNullPrice.setNombre("Producto Sin Precio");
        productWithNullPrice.setPrecio(null);
        productWithNullPrice.setDescripcion("Producto sin precio");
        productWithNullPrice.setEliminado(false);

        when(productClient.getProductById(productId)).thenReturn(productWithNullPrice);

        // When
        ProductResponse result = productClient.getProductById(productId);

        // Then
        assertNotNull(result);
        assertEquals(productWithNullPrice.getIdProducto(), result.getIdProducto());
        assertEquals(productWithNullPrice.getNombre(), result.getNombre());
        assertNull(result.getPrecio());
        assertEquals(productWithNullPrice.getDescripcion(), result.getDescripcion());
        assertEquals(productWithNullPrice.getEliminado(), result.getEliminado());
        
        verify(productClient).getProductById(productId);
    }

    @Test
    @DisplayName("getProductById - Debe retornar producto con todos los campos")
    void getProductById_WhenProductHasAllFields_ShouldReturnCompleteProduct() {
        // Given
        Long productId = 4L;
        ProductResponse completeProduct = new ProductResponse();
        completeProduct.setIdProducto(productId);
        completeProduct.setNombre("Producto Completo");
        completeProduct.setPrecio(150.0);
        completeProduct.setDescripcion("Descripción completa del producto");
        completeProduct.setEliminado(false);
        completeProduct.setFechaCreacion("2025-01-01T10:00:00");
        completeProduct.setFechaModificacion("2025-01-01T11:00:00");

        when(productClient.getProductById(productId)).thenReturn(completeProduct);

        // When
        ProductResponse result = productClient.getProductById(productId);

        // Then
        assertNotNull(result);
        assertEquals(completeProduct.getIdProducto(), result.getIdProducto());
        assertEquals(completeProduct.getNombre(), result.getNombre());
        assertEquals(completeProduct.getPrecio(), result.getPrecio());
        assertEquals(completeProduct.getDescripcion(), result.getDescripcion());
        assertEquals(completeProduct.getEliminado(), result.getEliminado());
        assertEquals(completeProduct.getFechaCreacion(), result.getFechaCreacion());
        assertEquals(completeProduct.getFechaModificacion(), result.getFechaModificacion());
        
        verify(productClient).getProductById(productId);
    }

    @Test
    @DisplayName("getProductById - Debe manejar diferentes IDs de producto")
    void getProductById_WithDifferentProductIds_ShouldHandleCorrectly() {
        // Given
        Long productId1 = 1L;
        Long productId2 = 2L;
        Long productId3 = 3L;

        ProductResponse product1 = new ProductResponse();
        product1.setIdProducto(productId1);
        product1.setNombre("Producto 1");
        product1.setPrecio(100.0);
        product1.setEliminado(false);

        ProductResponse product2 = new ProductResponse();
        product2.setIdProducto(productId2);
        product2.setNombre("Producto 2");
        product2.setPrecio(200.0);
        product2.setEliminado(false);

        when(productClient.getProductById(productId1)).thenReturn(product1);
        when(productClient.getProductById(productId2)).thenReturn(product2);
        when(productClient.getProductById(productId3)).thenReturn(null);

        // When & Then
        ProductResponse result1 = productClient.getProductById(productId1);
        ProductResponse result2 = productClient.getProductById(productId2);
        ProductResponse result3 = productClient.getProductById(productId3);

        assertNotNull(result1);
        assertEquals(productId1, result1.getIdProducto());
        assertEquals("Producto 1", result1.getNombre());

        assertNotNull(result2);
        assertEquals(productId2, result2.getIdProducto());
        assertEquals("Producto 2", result2.getNombre());

        assertNull(result3);

        verify(productClient).getProductById(productId1);
        verify(productClient).getProductById(productId2);
        verify(productClient).getProductById(productId3);
    }

    @Test
    @DisplayName("getProductById - Debe verificar que se llama con el ID correcto")
    void getProductById_ShouldCallWithCorrectId() {
        // Given
        Long productId = 5L;
        ProductResponse product = new ProductResponse();
        product.setIdProducto(productId);
        product.setNombre("Producto Test");
        product.setPrecio(75.0);
        product.setEliminado(false);

        when(productClient.getProductById(productId)).thenReturn(product);

        // When
        ProductResponse result = productClient.getProductById(productId);

        // Then
        assertNotNull(result);
        verify(productClient).getProductById(productId);
    }

    @Test
    @DisplayName("getProductById - Debe manejar ID nulo")
    void getProductById_WithNullId_ShouldHandleCorrectly() {
        // Given
        Long productId = null;
        when(productClient.getProductById(productId)).thenReturn(null);

        // When
        ProductResponse result = productClient.getProductById(productId);

        // Then
        assertNull(result);
        verify(productClient).getProductById(productId);
    }

    @Test
    @DisplayName("getProductById - Debe manejar ID cero")
    void getProductById_WithZeroId_ShouldHandleCorrectly() {
        // Given
        Long productId = 0L;
        when(productClient.getProductById(productId)).thenReturn(null);

        // When
        ProductResponse result = productClient.getProductById(productId);

        // Then
        assertNull(result);
        verify(productClient).getProductById(productId);
    }

    @Test
    @DisplayName("getProductById - Debe manejar ID negativo")
    void getProductById_WithNegativeId_ShouldHandleCorrectly() {
        // Given
        Long productId = -1L;
        when(productClient.getProductById(productId)).thenReturn(null);

        // When
        ProductResponse result = productClient.getProductById(productId);

        // Then
        assertNull(result);
        verify(productClient).getProductById(productId);
    }
}
