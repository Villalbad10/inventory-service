package com.inventory_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory_service.client.dto.ProductResponse;
import com.inventory_service.dto.BuyRequest;
import com.inventory_service.dto.BuyResponse;
import com.inventory_service.dto.UpdateQuantityRequest;
import com.inventory_service.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para InventoryController.
 * 
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryController Tests")
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private ProductResponse validProduct;
    private BuyRequest validBuyRequest;
    private BuyResponse validBuyResponse;
    private UpdateQuantityRequest validUpdateRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController)
                .setControllerAdvice(new com.inventory_service.exception.ApiExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        // Configurar producto válido
        validProduct = new ProductResponse();
        validProduct.setIdProducto(1L);
        validProduct.setNombre("Producto Test");
        validProduct.setPrecio(100.0);
        validProduct.setDescripcion("Descripción del producto");
        validProduct.setEliminado(false);

        // Configurar request de compra válido
        validBuyRequest = BuyRequest.builder()
                .productId(1L)
                .quantity(5)
                .build();

        // Configurar response de compra válido
        validBuyResponse = BuyResponse.builder()
                .productId(1L)
                .productName("Producto Test")
                .quantityPurchased(5)
                .remainingQuantity(5)
                .unitPrice(100.0)
                .totalAmount(500.0)
                .buyDate(LocalDateTime.now())
                .message("Compra exitosa de 5 unidades de Producto Test")
                .build();

        // Configurar request de actualización válido
        validUpdateRequest = new UpdateQuantityRequest();
        validUpdateRequest.setCantidad(15);
    }

    @Test
    @DisplayName("GET /api/v1/inventory/{productId}/available - Debe retornar cantidad disponible")
    void getAvailable_WhenValidProductId_ShouldReturnAvailableQuantity() throws Exception {
        // Given
        Long productId = 1L;
        Integer availableQuantity = 10;
        when(inventoryService.getAvailableQuantityByProductId(productId)).thenReturn(availableQuantity);

        // When & Then
        mockMvc.perform(get("/api/v1/inventory/{productId}/available", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(availableQuantity));
    }

    @Test
    @DisplayName("GET /api/v1/inventory/{productId} - Debe retornar producto")
    void getProduct_WhenValidProductId_ShouldReturnProduct() throws Exception {
        // Given
        Long productId = 1L;
        when(inventoryService.getProductById(productId)).thenReturn(validProduct);

        // When & Then
        mockMvc.perform(get("/api/v1/inventory/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idProducto").value(validProduct.getIdProducto()))
                .andExpect(jsonPath("$.nombre").value(validProduct.getNombre()))
                .andExpect(jsonPath("$.precio").value(validProduct.getPrecio()))
                .andExpect(jsonPath("$.descripcion").value(validProduct.getDescripcion()))
                .andExpect(jsonPath("$.eliminado").value(validProduct.getEliminado()));
    }

    @Test
    @DisplayName("PUT /api/v1/inventory/update/{productId} - Debe actualizar cantidad disponible")
    void updateAvailable_WhenValidRequest_ShouldUpdateQuantity() throws Exception {
        // Given
        Long productId = 1L;
        Integer updatedQuantity = 15;
        when(inventoryService.updateAvailableQuantity(productId, validUpdateRequest.getCantidad()))
                .thenReturn(updatedQuantity);

        // When & Then
        mockMvc.perform(put("/api/v1/inventory/update/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(updatedQuantity));
    }

    @Test
    @DisplayName("PUT /api/v1/inventory/update/{productId} - Debe validar request body")
    void updateAvailable_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        Long productId = 1L;
        UpdateQuantityRequest invalidRequest = new UpdateQuantityRequest();
        invalidRequest.setCantidad(-1); // Cantidad negativa

        // When & Then
        mockMvc.perform(put("/api/v1/inventory/update/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/inventory/update/{productId} - Debe validar request body nulo")
    void updateAvailable_WhenNullRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        Long productId = 1L;

        // When & Then
        mockMvc.perform(put("/api/v1/inventory/update/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/inventory/buy - Debe procesar compra exitosamente")
    void buyProduct_WhenValidRequest_ShouldProcessPurchase() throws Exception {
        // Given
        when(inventoryService.buyProduct(any(BuyRequest.class))).thenReturn(validBuyResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/inventory/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBuyRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(validBuyResponse.getProductId()))
                .andExpect(jsonPath("$.productName").value(validBuyResponse.getProductName()))
                .andExpect(jsonPath("$.quantityPurchased").value(validBuyResponse.getQuantityPurchased()))
                .andExpect(jsonPath("$.remainingQuantity").value(validBuyResponse.getRemainingQuantity()))
                .andExpect(jsonPath("$.unitPrice").value(validBuyResponse.getUnitPrice()))
                .andExpect(jsonPath("$.totalAmount").value(validBuyResponse.getTotalAmount()))
                .andExpect(jsonPath("$.message").value(validBuyResponse.getMessage()))
                .andExpect(jsonPath("$.buyDate").exists());
    }

    @Test
    @DisplayName("POST /api/v1/inventory/buy - Debe validar request body")
    void buyProduct_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
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
    @DisplayName("POST /api/v1/inventory/buy - Debe validar cantidad negativa")
    void buyProduct_WhenNegativeQuantity_ShouldReturnBadRequest() throws Exception {
        // Given
        BuyRequest invalidRequest = BuyRequest.builder()
                .productId(1L)
                .quantity(-1) // Cantidad negativa
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/inventory/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/inventory/buy - Debe validar cantidad cero")
    void buyProduct_WhenZeroQuantity_ShouldReturnBadRequest() throws Exception {
        // Given
        BuyRequest invalidRequest = BuyRequest.builder()
                .productId(1L)
                .quantity(0) // Cantidad cero
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/inventory/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/inventory/buy - Debe validar request body nulo")
    void buyProduct_WhenNullRequest_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/inventory/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/inventory/{productId}/available - Debe manejar producto inexistente")
    void getAvailable_WhenProductNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        Long productId = 999L;
        when(inventoryService.getAvailableQuantityByProductId(productId))
                .thenThrow(new com.inventory_service.exception.NotFoundException("Producto no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/v1/inventory/{productId}/available", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/inventory/{productId} - Debe manejar producto inexistente")
    void getProduct_WhenProductNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        Long productId = 999L;
        when(inventoryService.getProductById(productId))
                .thenThrow(new com.inventory_service.exception.NotFoundException("Producto no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/v1/inventory/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/inventory/update/{productId} - Debe manejar producto inexistente")
    void updateAvailable_WhenProductNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        Long productId = 999L;
        when(inventoryService.updateAvailableQuantity(productId, validUpdateRequest.getCantidad()))
                .thenThrow(new com.inventory_service.exception.NotFoundException("Producto no encontrado"));

        // When & Then
        mockMvc.perform(put("/api/v1/inventory/update/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/inventory/buy - Debe manejar inventario insuficiente")
    void buyProduct_WhenInsufficientInventory_ShouldReturnBadRequest() throws Exception {
        // Given
        when(inventoryService.buyProduct(any(BuyRequest.class)))
                .thenThrow(new com.inventory_service.exception.BadRequestException("Inventario insuficiente"));

        // When & Then
        mockMvc.perform(post("/api/v1/inventory/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBuyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/inventory/buy - Debe manejar producto inexistente")
    void buyProduct_WhenProductNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(inventoryService.buyProduct(any(BuyRequest.class)))
                .thenThrow(new com.inventory_service.exception.NotFoundException("Producto no encontrado"));

        // When & Then
        mockMvc.perform(post("/api/v1/inventory/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBuyRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/inventory/{productId}/available - Debe validar parámetro de ruta")
    void getAvailable_WhenInvalidPathParameter_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/inventory/invalid/available")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/inventory/{productId} - Debe validar parámetro de ruta")
    void getProduct_WhenInvalidPathParameter_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/inventory/invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/inventory/update/{productId} - Debe validar parámetro de ruta")
    void updateAvailable_WhenInvalidPathParameter_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/inventory/update/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isBadRequest());
    }
}
