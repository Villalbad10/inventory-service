package com.inventory_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory_service.client.ProductClient;
import com.inventory_service.client.dto.ProductResponse;
import com.inventory_service.dto.BuyRequest;
import com.inventory_service.dto.BuyResponse;
import com.inventory_service.dto.UpdateQuantityRequest;
import com.inventory_service.model.Inventory;
import com.inventory_service.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para la aplicación completa.
 * 
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("Integration Tests")
class InventoryServiceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductClient productClient;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        inventoryRepository.deleteAll();
        
        // Mock ProductClient responses
        ProductResponse validProduct = new ProductResponse();
        validProduct.setIdProducto(1L);
        validProduct.setNombre("Producto Test");
        validProduct.setPrecio(100.0);
        validProduct.setDescripcion("Descripción del producto");
        validProduct.setEliminado(false);
        
        when(productClient.getProductById(1L)).thenReturn(validProduct);
        when(productClient.getProductById(2L)).thenReturn(validProduct);
        when(productClient.getProductById(3L)).thenReturn(validProduct);
        when(productClient.getProductById(4L)).thenReturn(validProduct);
        when(productClient.getProductById(5L)).thenReturn(validProduct);
        when(productClient.getProductById(6L)).thenReturn(validProduct);
        when(productClient.getProductById(7L)).thenReturn(validProduct);
        when(productClient.getProductById(8L)).thenReturn(validProduct);
        when(productClient.getProductById(9L)).thenReturn(validProduct);
        when(productClient.getProductById(10L)).thenReturn(validProduct);
    }

    @Test
    @DisplayName("Integration - Debe procesar flujo completo de compra exitosamente")
    void integration_CompletePurchaseFlow_ShouldWorkSuccessfully() throws Exception {
        // Given
        Long productId = 1L;
        Integer initialQuantity = 20;
        Integer purchaseQuantity = 5;

        // Crear inventario inicial
        Inventory inventory = Inventory.builder()
                .productoId(productId)
                .cantidad(initialQuantity)
                .eliminado(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();
        inventoryRepository.save(inventory);

        BuyRequest buyRequest = BuyRequest.builder()
                .productId(productId)
                .quantity(purchaseQuantity)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/inventory/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.quantityPurchased").value(purchaseQuantity))
                .andExpect(jsonPath("$.remainingQuantity").value(initialQuantity - purchaseQuantity))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.buyDate").exists());

        // Verificar que el inventario se actualizó correctamente
        Inventory updatedInventory = inventoryRepository.findByProductoIdAndEliminadoFalse(productId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
        assertEquals(Integer.valueOf(initialQuantity - purchaseQuantity), updatedInventory.getCantidad());
    }

    @Test
    @DisplayName("Integration - Debe actualizar cantidad disponible correctamente")
    void integration_UpdateAvailableQuantity_ShouldWorkSuccessfully() throws Exception {
        // Given
        Long productId = 2L;
        Integer newQuantity = 30;

        UpdateQuantityRequest updateRequest = new UpdateQuantityRequest();
        updateRequest.setCantidad(newQuantity);

        // When & Then
        mockMvc.perform(put("/api/v1/inventory/update/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(newQuantity));

        // Verificar que el inventario se creó/actualizó correctamente
        Inventory inventory = inventoryRepository.findByProductoIdAndEliminadoFalse(productId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
        assertEquals(Integer.valueOf(newQuantity), inventory.getCantidad());
    }

    @Test
    @DisplayName("Integration - Debe consultar cantidad disponible correctamente")
    void integration_GetAvailableQuantity_ShouldWorkSuccessfully() throws Exception {
        // Given
        Long productId = 3L;
        Integer quantity = 15;

        Inventory inventory = Inventory.builder()
                .productoId(productId)
                .cantidad(quantity)
                .eliminado(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();
        inventoryRepository.save(inventory);

        // When & Then
        mockMvc.perform(get("/api/v1/inventory/{productId}/available", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(quantity));
    }

    @Test
    @DisplayName("Integration - Debe manejar inventario insuficiente correctamente")
    void integration_InsufficientInventory_ShouldReturnBadRequest() throws Exception {
        // Given
        Long productId = 4L;
        Integer availableQuantity = 5;
        Integer requestedQuantity = 10;

        Inventory inventory = Inventory.builder()
                .productoId(productId)
                .cantidad(availableQuantity)
                .eliminado(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();
        inventoryRepository.save(inventory);

        BuyRequest buyRequest = BuyRequest.builder()
                .productId(productId)
                .quantity(requestedQuantity)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/inventory/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration - Debe manejar producto inexistente correctamente")
    void integration_NonExistentProduct_ShouldReturnNotFound() throws Exception {
        // Given
        Long nonExistentProductId = 999L;

        // When & Then
        mockMvc.perform(get("/api/v1/inventory/{productId}/available", nonExistentProductId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Integration - Debe manejar validación de request body correctamente")
    void integration_InvalidRequestBody_ShouldReturnBadRequest() throws Exception {
        // Given
        Long productId = 5L;
        BuyRequest invalidRequest = BuyRequest.builder()
                .productId(null) // ID nulo
                .quantity(5)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/inventory/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration - Debe manejar validación de cantidad negativa correctamente")
    void integration_NegativeQuantity_ShouldReturnBadRequest() throws Exception {
        // Given
        Long productId = 6L;
        BuyRequest invalidRequest = BuyRequest.builder()
                .productId(productId)
                .quantity(-1) // Cantidad negativa
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/inventory/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration - Debe manejar validación de cantidad cero correctamente")
    void integration_ZeroQuantity_ShouldReturnBadRequest() throws Exception {
        // Given
        Long productId = 7L;
        BuyRequest invalidRequest = BuyRequest.builder()
                .productId(productId)
                .quantity(0) // Cantidad cero
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/inventory/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration - Debe manejar validación de UpdateQuantityRequest correctamente")
    void integration_InvalidUpdateQuantityRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        Long productId = 8L;
        UpdateQuantityRequest invalidRequest = new UpdateQuantityRequest();
        invalidRequest.setCantidad(-1); // Cantidad negativa

        // When & Then
        mockMvc.perform(put("/api/v1/inventory/update/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration - Debe manejar parámetros de ruta inválidos correctamente")
    void integration_InvalidPathParameters_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/inventory/invalid/available")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/inventory/invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/v1/inventory/update/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateQuantityRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration - Debe manejar múltiples operaciones correctamente")
    void integration_MultipleOperations_ShouldWorkCorrectly() throws Exception {
        // Given
        Long productId = 9L;
        Integer initialQuantity = 100;

        // 1. Crear inventario inicial
        UpdateQuantityRequest createRequest = new UpdateQuantityRequest();
        createRequest.setCantidad(initialQuantity);

        mockMvc.perform(put("/api/v1/inventory/update/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(initialQuantity));

        // 2. Consultar cantidad disponible
        mockMvc.perform(get("/api/v1/inventory/{productId}/available", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(initialQuantity));

        // 3. Realizar compra
        BuyRequest buyRequest = BuyRequest.builder()
                .productId(productId)
                .quantity(25)
                .build();

        mockMvc.perform(post("/api/v1/inventory/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainingQuantity").value(initialQuantity - 25));

        // 4. Verificar cantidad actualizada
        mockMvc.perform(get("/api/v1/inventory/{productId}/available", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(initialQuantity - 25));

        // 5. Actualizar cantidad nuevamente
        UpdateQuantityRequest updateRequest = new UpdateQuantityRequest();
        updateRequest.setCantidad(50);

        mockMvc.perform(put("/api/v1/inventory/update/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(50));

        // 6. Verificar cantidad final
        mockMvc.perform(get("/api/v1/inventory/{productId}/available", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(50));
    }

    @Test
    @DisplayName("Integration - Debe manejar transacciones correctamente")
    void integration_Transactions_ShouldWorkCorrectly() throws Exception {
        // Given
        Long productId = 10L;
        Integer initialQuantity = 10;

        // Crear inventario inicial
        Inventory inventory = Inventory.builder()
                .productoId(productId)
                .cantidad(initialQuantity)
                .eliminado(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();
        inventoryRepository.save(inventory);

        // Intentar compra que debería fallar por inventario insuficiente
        BuyRequest buyRequest = BuyRequest.builder()
                .productId(productId)
                .quantity(15) // Más que el disponible
                .build();

        mockMvc.perform(post("/api/v1/inventory/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyRequest)))
                .andExpect(status().isBadRequest());

        // Verificar que el inventario no cambió
        Inventory unchangedInventory = inventoryRepository.findByProductoIdAndEliminadoFalse(productId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
        assertEquals(Integer.valueOf(initialQuantity), unchangedInventory.getCantidad());
    }
}
