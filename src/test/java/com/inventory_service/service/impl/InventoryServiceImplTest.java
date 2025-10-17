package com.inventory_service.service.impl;

import com.inventory_service.client.ProductClient;
import com.inventory_service.client.dto.ProductResponse;
import com.inventory_service.dto.BuyRequest;
import com.inventory_service.dto.BuyResponse;
import com.inventory_service.exception.BadRequestException;
import com.inventory_service.exception.NotFoundException;
import com.inventory_service.model.Inventory;
import com.inventory_service.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para InventoryServiceImpl.
 * 
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService Tests")
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private ProductResponse validProduct;
    private ProductResponse deletedProduct;
    private Inventory validInventory;
    private BuyRequest validBuyRequest;

    @BeforeEach
    void setUp() {
        // Configurar producto válido
        validProduct = new ProductResponse();
        validProduct.setIdProducto(1L);
        validProduct.setNombre("Producto Test");
        validProduct.setPrecio(100.0);
        validProduct.setDescripcion("Descripción del producto");
        validProduct.setEliminado(false);

        // Configurar producto eliminado
        deletedProduct = new ProductResponse();
        deletedProduct.setIdProducto(2L);
        deletedProduct.setNombre("Producto Eliminado");
        deletedProduct.setPrecio(50.0);
        deletedProduct.setEliminado(true);

        // Configurar inventario válido
        validInventory = Inventory.builder()
                .idInventario(1L)
                .productoId(1L)
                .cantidad(10)
                .eliminado(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();

        // Configurar request de compra válido
        validBuyRequest = BuyRequest.builder()
                .productId(1L)
                .quantity(5)
                .build();
    }

    @Test
    @DisplayName("getAvailableQuantityByProductId - Debe retornar cantidad cuando producto e inventario existen")
    void getAvailableQuantityByProductId_WhenProductAndInventoryExist_ShouldReturnQuantity() {
        // Given
        Long productId = 1L;
        when(productClient.getProductById(productId)).thenReturn(validProduct);
        when(inventoryRepository.findByProductoIdAndEliminadoFalse(productId))
                .thenReturn(Optional.of(validInventory));

        // When
        Integer result = inventoryService.getAvailableQuantityByProductId(productId);

        // Then
        assertEquals(10, result);
        verify(productClient).getProductById(productId);
        verify(inventoryRepository).findByProductoIdAndEliminadoFalse(productId);
    }

    @Test
    @DisplayName("getAvailableQuantityByProductId - Debe lanzar NotFoundException cuando producto no existe")
    void getAvailableQuantityByProductId_WhenProductNotFound_ShouldThrowNotFoundException() {
        // Given
        Long productId = 999L;
        when(productClient.getProductById(productId)).thenReturn(null);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> inventoryService.getAvailableQuantityByProductId(productId));
        
        assertEquals("Producto no encontrado en product-service", exception.getMessage());
        verify(productClient).getProductById(productId);
        verify(inventoryRepository, never()).findByProductoIdAndEliminadoFalse(anyLong());
    }

    @Test
    @DisplayName("getAvailableQuantityByProductId - Debe lanzar NotFoundException cuando producto está eliminado")
    void getAvailableQuantityByProductId_WhenProductIsDeleted_ShouldThrowNotFoundException() {
        // Given
        Long productId = 2L;
        when(productClient.getProductById(productId)).thenReturn(deletedProduct);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> inventoryService.getAvailableQuantityByProductId(productId));
        
        assertEquals("Producto no encontrado en product-service", exception.getMessage());
        verify(productClient).getProductById(productId);
        verify(inventoryRepository, never()).findByProductoIdAndEliminadoFalse(anyLong());
    }

    @Test
    @DisplayName("getAvailableQuantityByProductId - Debe lanzar NotFoundException cuando inventario no existe")
    void getAvailableQuantityByProductId_WhenInventoryNotFound_ShouldThrowNotFoundException() {
        // Given
        Long productId = 1L;
        when(productClient.getProductById(productId)).thenReturn(validProduct);
        when(inventoryRepository.findByProductoIdAndEliminadoFalse(productId))
                .thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> inventoryService.getAvailableQuantityByProductId(productId));
        
        assertEquals("Inventario no encontrado para el producto", exception.getMessage());
        verify(productClient).getProductById(productId);
        verify(inventoryRepository).findByProductoIdAndEliminadoFalse(productId);
    }

    @Test
    @DisplayName("updateAvailableQuantity - Debe actualizar cantidad cuando producto e inventario existen")
    void updateAvailableQuantity_WhenProductAndInventoryExist_ShouldUpdateQuantity() {
        // Given
        Long productId = 1L;
        Integer newQuantity = 15;
        when(productClient.getProductById(productId)).thenReturn(validProduct);
        when(inventoryRepository.findByProductoIdAndEliminadoFalse(productId))
                .thenReturn(Optional.of(validInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(validInventory);

        // When
        Integer result = inventoryService.updateAvailableQuantity(productId, newQuantity);

        // Then
        assertEquals(newQuantity, result);
        assertEquals(newQuantity, validInventory.getCantidad());
        verify(productClient).getProductById(productId);
        verify(inventoryRepository).findByProductoIdAndEliminadoFalse(productId);
        verify(inventoryRepository).save(validInventory);
    }

    @Test
    @DisplayName("updateAvailableQuantity - Debe crear inventario cuando producto existe pero inventario no")
    void updateAvailableQuantity_WhenProductExistsButInventoryNot_ShouldCreateInventory() {
        // Given
        Long productId = 1L;
        Integer newQuantity = 20;
        when(productClient.getProductById(productId)).thenReturn(validProduct);
        when(inventoryRepository.findByProductoIdAndEliminadoFalse(productId))
                .thenReturn(Optional.empty());
        
        Inventory newInventory = Inventory.builder()
                .productoId(productId)
                .cantidad(newQuantity)
                .eliminado(false)
                .build();
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(newInventory);

        // When
        Integer result = inventoryService.updateAvailableQuantity(productId, newQuantity);

        // Then
        assertEquals(newQuantity, result);
        verify(productClient).getProductById(productId);
        verify(inventoryRepository).findByProductoIdAndEliminadoFalse(productId);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    @DisplayName("updateAvailableQuantity - Debe lanzar NotFoundException cuando producto no existe")
    void updateAvailableQuantity_WhenProductNotFound_ShouldThrowNotFoundException() {
        // Given
        Long productId = 999L;
        Integer newQuantity = 10;
        when(productClient.getProductById(productId)).thenReturn(null);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> inventoryService.updateAvailableQuantity(productId, newQuantity));
        
        assertEquals("Producto no encontrado en product-service", exception.getMessage());
        verify(productClient).getProductById(productId);
        verify(inventoryRepository, never()).findByProductoIdAndEliminadoFalse(anyLong());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("getProductById - Debe retornar producto cuando existe y no está eliminado")
    void getProductById_WhenProductExistsAndNotDeleted_ShouldReturnProduct() {
        // Given
        Long productId = 1L;
        when(productClient.getProductById(productId)).thenReturn(validProduct);

        // When
        ProductResponse result = inventoryService.getProductById(productId);

        // Then
        assertNotNull(result);
        assertEquals(validProduct.getIdProducto(), result.getIdProducto());
        assertEquals(validProduct.getNombre(), result.getNombre());
        assertEquals(validProduct.getPrecio(), result.getPrecio());
        verify(productClient).getProductById(productId);
    }

    @Test
    @DisplayName("getProductById - Debe lanzar NotFoundException cuando producto no existe")
    void getProductById_WhenProductNotFound_ShouldThrowNotFoundException() {
        // Given
        Long productId = 999L;
        when(productClient.getProductById(productId)).thenReturn(null);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> inventoryService.getProductById(productId));
        
        assertEquals("Producto no encontrado en product-service", exception.getMessage());
        verify(productClient).getProductById(productId);
    }

    @Test
    @DisplayName("getProductById - Debe lanzar NotFoundException cuando producto está eliminado")
    void getProductById_WhenProductIsDeleted_ShouldThrowNotFoundException() {
        // Given
        Long productId = 2L;
        when(productClient.getProductById(productId)).thenReturn(deletedProduct);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> inventoryService.getProductById(productId));
        
        assertEquals("Producto no encontrado en product-service", exception.getMessage());
        verify(productClient).getProductById(productId);
    }

    @Test
    @DisplayName("buyProduct - Debe procesar compra exitosamente cuando hay inventario suficiente")
    void buyProduct_WhenSufficientInventory_ShouldProcessPurchaseSuccessfully() {
        // Given
        when(productClient.getProductById(validBuyRequest.getProductId())).thenReturn(validProduct);
        when(inventoryRepository.findByProductoIdAndEliminadoFalse(validBuyRequest.getProductId()))
                .thenReturn(Optional.of(validInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(validInventory);

        // When
        BuyResponse result = inventoryService.buyProduct(validBuyRequest);

        // Then
        assertNotNull(result);
        assertEquals(validBuyRequest.getProductId(), result.getProductId());
        assertEquals(validProduct.getNombre(), result.getProductName());
        assertEquals(validBuyRequest.getQuantity(), result.getQuantityPurchased());
        assertEquals(5, result.getRemainingQuantity()); // 10 - 5 = 5
        assertEquals(validProduct.getPrecio(), result.getUnitPrice());
        assertEquals(500.0, result.getTotalAmount()); // 100.0 * 5 = 500.0
        assertNotNull(result.getBuyDate());
        assertTrue(result.getMessage().contains("Compra exitosa"));
        
        verify(productClient).getProductById(validBuyRequest.getProductId());
        verify(inventoryRepository).findByProductoIdAndEliminadoFalse(validBuyRequest.getProductId());
        verify(inventoryRepository).save(validInventory);
    }

    @Test
    @DisplayName("buyProduct - Debe lanzar NotFoundException cuando producto no existe")
    void buyProduct_WhenProductNotFound_ShouldThrowNotFoundException() {
        // Given
        when(productClient.getProductById(validBuyRequest.getProductId())).thenReturn(null);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> inventoryService.buyProduct(validBuyRequest));
        
        assertEquals("Producto no encontrado en product-service", exception.getMessage());
        verify(productClient).getProductById(validBuyRequest.getProductId());
        verify(inventoryRepository, never()).findByProductoIdAndEliminadoFalse(anyLong());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("buyProduct - Debe lanzar NotFoundException cuando inventario no existe")
    void buyProduct_WhenInventoryNotFound_ShouldThrowNotFoundException() {
        // Given
        when(productClient.getProductById(validBuyRequest.getProductId())).thenReturn(validProduct);
        when(inventoryRepository.findByProductoIdAndEliminadoFalse(validBuyRequest.getProductId()))
                .thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> inventoryService.buyProduct(validBuyRequest));
        
        assertEquals("Inventario no encontrado para el producto", exception.getMessage());
        verify(productClient).getProductById(validBuyRequest.getProductId());
        verify(inventoryRepository).findByProductoIdAndEliminadoFalse(validBuyRequest.getProductId());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("buyProduct - Debe lanzar BadRequestException cuando inventario es insuficiente")
    void buyProduct_WhenInsufficientInventory_ShouldThrowBadRequestException() {
        // Given
        BuyRequest requestWithHighQuantity = BuyRequest.builder()
                .productId(1L)
                .quantity(20) // Más que el inventario disponible (10)
                .build();
        
        when(productClient.getProductById(requestWithHighQuantity.getProductId())).thenReturn(validProduct);
        when(inventoryRepository.findByProductoIdAndEliminadoFalse(requestWithHighQuantity.getProductId()))
                .thenReturn(Optional.of(validInventory));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> inventoryService.buyProduct(requestWithHighQuantity));
        
        assertTrue(exception.getMessage().contains("Inventario insuficiente"));
        assertTrue(exception.getMessage().contains("Disponible: 10"));
        assertTrue(exception.getMessage().contains("Solicitado: 20"));
        
        verify(productClient).getProductById(requestWithHighQuantity.getProductId());
        verify(inventoryRepository).findByProductoIdAndEliminadoFalse(requestWithHighQuantity.getProductId());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("buyProduct - Debe manejar precio nulo correctamente")
    void buyProduct_WhenProductPriceIsNull_ShouldHandleCorrectly() {
        // Given
        ProductResponse productWithNullPrice = new ProductResponse();
        productWithNullPrice.setIdProducto(1L);
        productWithNullPrice.setNombre("Producto Sin Precio");
        productWithNullPrice.setPrecio(null);
        productWithNullPrice.setEliminado(false);

        when(productClient.getProductById(validBuyRequest.getProductId())).thenReturn(productWithNullPrice);
        when(inventoryRepository.findByProductoIdAndEliminadoFalse(validBuyRequest.getProductId()))
                .thenReturn(Optional.of(validInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(validInventory);

        // When
        BuyResponse result = inventoryService.buyProduct(validBuyRequest);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getUnitPrice());
        assertEquals(0.0, result.getTotalAmount());
        verify(productClient).getProductById(validBuyRequest.getProductId());
        verify(inventoryRepository).findByProductoIdAndEliminadoFalse(validBuyRequest.getProductId());
        verify(inventoryRepository).save(validInventory);
    }

    @Test
    @DisplayName("buyProduct - Debe lanzar NotFoundException cuando producto está eliminado")
    void buyProduct_WhenProductIsDeleted_ShouldThrowNotFoundException() {
        // Given
        when(productClient.getProductById(validBuyRequest.getProductId())).thenReturn(deletedProduct);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> inventoryService.buyProduct(validBuyRequest));
        
        assertEquals("Producto no encontrado en product-service", exception.getMessage());
        verify(productClient).getProductById(validBuyRequest.getProductId());
        verify(inventoryRepository, never()).findByProductoIdAndEliminadoFalse(anyLong());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }
}
