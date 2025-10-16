package com.inventory_service.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para los DTOs.
 * 
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@DisplayName("DTOs Tests")
class DTOsTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("BuyRequest - Debe crear request válido")
    void buyRequest_WithValidData_ShouldCreateValidRequest() {
        // Given
        Long productId = 1L;
        Integer quantity = 5;

        // When
        BuyRequest request = BuyRequest.builder()
                .productId(productId)
                .quantity(quantity)
                .build();

        // Then
        assertNotNull(request);
        assertEquals(productId, request.getProductId());
        assertEquals(quantity, request.getQuantity());
    }

    @Test
    @DisplayName("BuyRequest - Debe validar productId nulo")
    void buyRequest_WithNullProductId_ShouldHaveValidationError() {
        // Given
        BuyRequest request = BuyRequest.builder()
                .productId(null)
                .quantity(5)
                .build();

        // When
        Set<ConstraintViolation<BuyRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("productId")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("obligatorio")));
    }

    @Test
    @DisplayName("BuyRequest - Debe validar quantity nulo")
    void buyRequest_WithNullQuantity_ShouldHaveValidationError() {
        // Given
        BuyRequest request = BuyRequest.builder()
                .productId(1L)
                .quantity(null)
                .build();

        // When
        Set<ConstraintViolation<BuyRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("obligatoria")));
    }

    @Test
    @DisplayName("BuyRequest - Debe validar quantity cero")
    void buyRequest_WithZeroQuantity_ShouldHaveValidationError() {
        // Given
        BuyRequest request = BuyRequest.builder()
                .productId(1L)
                .quantity(0)
                .build();

        // When
        Set<ConstraintViolation<BuyRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("mayor a cero")));
    }

    @Test
    @DisplayName("BuyRequest - Debe validar quantity negativo")
    void buyRequest_WithNegativeQuantity_ShouldHaveValidationError() {
        // Given
        BuyRequest request = BuyRequest.builder()
                .productId(1L)
                .quantity(-1)
                .build();

        // When
        Set<ConstraintViolation<BuyRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("mayor a cero")));
    }

    @Test
    @DisplayName("BuyRequest - Debe aceptar quantity positivo")
    void buyRequest_WithPositiveQuantity_ShouldBeValid() {
        // Given
        BuyRequest request = BuyRequest.builder()
                .productId(1L)
                .quantity(10)
                .build();

        // When
        Set<ConstraintViolation<BuyRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("BuyResponse - Debe crear response válido")
    void buyResponse_WithValidData_ShouldCreateValidResponse() {
        // Given
        Long productId = 1L;
        String productName = "Producto Test";
        Integer quantityPurchased = 5;
        Integer remainingQuantity = 5;
        Double unitPrice = 100.0;
        Double totalAmount = 500.0;
        String message = "Compra exitosa";

        // When
        BuyResponse response = BuyResponse.builder()
                .productId(productId)
                .productName(productName)
                .quantityPurchased(quantityPurchased)
                .remainingQuantity(remainingQuantity)
                .unitPrice(unitPrice)
                .totalAmount(totalAmount)
                .message(message)
                .build();

        // Then
        assertNotNull(response);
        assertEquals(productId, response.getProductId());
        assertEquals(productName, response.getProductName());
        assertEquals(quantityPurchased, response.getQuantityPurchased());
        assertEquals(remainingQuantity, response.getRemainingQuantity());
        assertEquals(unitPrice, response.getUnitPrice());
        assertEquals(totalAmount, response.getTotalAmount());
        assertEquals(message, response.getMessage());
        assertNull(response.getBuyDate()); // No se estableció en el builder
    }

    @Test
    @DisplayName("BuyResponse - Debe manejar campos nulos")
    void buyResponse_WithNullFields_ShouldHandleCorrectly() {
        // When
        BuyResponse response = BuyResponse.builder()
                .productId(null)
                .productName(null)
                .quantityPurchased(null)
                .remainingQuantity(null)
                .unitPrice(null)
                .totalAmount(null)
                .message(null)
                .build();

        // Then
        assertNotNull(response);
        assertNull(response.getProductId());
        assertNull(response.getProductName());
        assertNull(response.getQuantityPurchased());
        assertNull(response.getRemainingQuantity());
        assertNull(response.getUnitPrice());
        assertNull(response.getTotalAmount());
        assertNull(response.getMessage());
        assertNull(response.getBuyDate());
    }

    @Test
    @DisplayName("UpdateQuantityRequest - Debe crear request válido")
    void updateQuantityRequest_WithValidData_ShouldCreateValidRequest() {
        // Given
        Integer cantidad = 15;

        // When
        UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setCantidad(cantidad);

        // Then
        assertNotNull(request);
        assertEquals(cantidad, request.getCantidad());
    }

    @Test
    @DisplayName("UpdateQuantityRequest - Debe validar cantidad nula")
    void updateQuantityRequest_WithNullCantidad_ShouldHaveValidationError() {
        // Given
        UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setCantidad(null);

        // When
        Set<ConstraintViolation<UpdateQuantityRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cantidad")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("obligatoria")));
    }

    @Test
    @DisplayName("UpdateQuantityRequest - Debe validar cantidad negativa")
    void updateQuantityRequest_WithNegativeCantidad_ShouldHaveValidationError() {
        // Given
        UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setCantidad(-1);

        // When
        Set<ConstraintViolation<UpdateQuantityRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cantidad")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("cero o positiva")));
    }

    @Test
    @DisplayName("UpdateQuantityRequest - Debe aceptar cantidad cero")
    void updateQuantityRequest_WithZeroCantidad_ShouldBeValid() {
        // Given
        UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setCantidad(0);

        // When
        Set<ConstraintViolation<UpdateQuantityRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("UpdateQuantityRequest - Debe aceptar cantidad positiva")
    void updateQuantityRequest_WithPositiveCantidad_ShouldBeValid() {
        // Given
        UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setCantidad(100);

        // When
        Set<ConstraintViolation<UpdateQuantityRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("BuyRequest - Debe usar constructor por defecto")
    void buyRequest_WithDefaultConstructor_ShouldCreateEmptyRequest() {
        // When
        BuyRequest request = new BuyRequest();

        // Then
        assertNotNull(request);
        assertNull(request.getProductId());
        assertNull(request.getQuantity());
    }

    @Test
    @DisplayName("BuyRequest - Debe usar constructor con argumentos")
    void buyRequest_WithArgsConstructor_ShouldCreateRequestWithValues() {
        // Given
        Long productId = 2L;
        Integer quantity = 3;

        // When
        BuyRequest request = new BuyRequest(productId, quantity);

        // Then
        assertNotNull(request);
        assertEquals(productId, request.getProductId());
        assertEquals(quantity, request.getQuantity());
    }

    @Test
    @DisplayName("BuyResponse - Debe usar constructor por defecto")
    void buyResponse_WithDefaultConstructor_ShouldCreateEmptyResponse() {
        // When
        BuyResponse response = new BuyResponse();

        // Then
        assertNotNull(response);
        assertNull(response.getProductId());
        assertNull(response.getProductName());
        assertNull(response.getQuantityPurchased());
        assertNull(response.getRemainingQuantity());
        assertNull(response.getUnitPrice());
        assertNull(response.getTotalAmount());
        assertNull(response.getMessage());
        assertNull(response.getBuyDate());
    }

    @Test
    @DisplayName("BuyResponse - Debe usar constructor con argumentos")
    void buyResponse_WithArgsConstructor_ShouldCreateResponseWithValues() {
        // Given
        Long productId = 3L;
        String productName = "Producto Test";
        Integer quantityPurchased = 2;
        Integer remainingQuantity = 8;
        Double unitPrice = 50.0;
        Double totalAmount = 100.0;
        String message = "Compra realizada";

        // When
        BuyResponse response = new BuyResponse(productId, productName, quantityPurchased, 
                remainingQuantity, unitPrice, totalAmount, null, message);

        // Then
        assertNotNull(response);
        assertEquals(productId, response.getProductId());
        assertEquals(productName, response.getProductName());
        assertEquals(quantityPurchased, response.getQuantityPurchased());
        assertEquals(remainingQuantity, response.getRemainingQuantity());
        assertEquals(unitPrice, response.getUnitPrice());
        assertEquals(totalAmount, response.getTotalAmount());
        assertEquals(message, response.getMessage());
        assertNull(response.getBuyDate());
    }

    @Test
    @DisplayName("BuyRequest - Debe usar setters y getters")
    void buyRequest_WithSettersAndGetters_ShouldWorkCorrectly() {
        // Given
        BuyRequest request = new BuyRequest();
        Long productId = 4L;
        Integer quantity = 7;

        // When
        request.setProductId(productId);
        request.setQuantity(quantity);

        // Then
        assertEquals(productId, request.getProductId());
        assertEquals(quantity, request.getQuantity());
    }

    @Test
    @DisplayName("BuyResponse - Debe usar setters y getters")
    void buyResponse_WithSettersAndGetters_ShouldWorkCorrectly() {
        // Given
        BuyResponse response = new BuyResponse();
        Long productId = 5L;
        String productName = "Producto Test";
        Integer quantityPurchased = 1;
        Integer remainingQuantity = 9;
        Double unitPrice = 75.0;
        Double totalAmount = 75.0;
        String message = "Compra única";

        // When
        response.setProductId(productId);
        response.setProductName(productName);
        response.setQuantityPurchased(quantityPurchased);
        response.setRemainingQuantity(remainingQuantity);
        response.setUnitPrice(unitPrice);
        response.setTotalAmount(totalAmount);
        response.setMessage(message);

        // Then
        assertEquals(productId, response.getProductId());
        assertEquals(productName, response.getProductName());
        assertEquals(quantityPurchased, response.getQuantityPurchased());
        assertEquals(remainingQuantity, response.getRemainingQuantity());
        assertEquals(unitPrice, response.getUnitPrice());
        assertEquals(totalAmount, response.getTotalAmount());
        assertEquals(message, response.getMessage());
    }
}
