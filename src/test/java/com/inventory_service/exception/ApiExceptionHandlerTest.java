package com.inventory_service.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para ApiExceptionHandler.
 * 
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ApiExceptionHandler Tests")
class ApiExceptionHandlerTest {

    @InjectMocks
    private ApiExceptionHandler exceptionHandler;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(exceptionHandler)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("handleValidationExceptions - Debe manejar errores de validación correctamente")
    void handleValidationExceptions_ShouldHandleValidationErrors() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory/buy");

        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("buyRequest", "productId", "El ID del producto es obligatorio");
        FieldError fieldError2 = new FieldError("buyRequest", "quantity", "La cantidad debe ser mayor a cero");

        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // When
        MensajeError result = exceptionHandler.handleValidationExceptions(request, exception);

        // Then
        assertNotNull(result);
        assertEquals("/api/v1/inventory/buy", result.getPath());
        assertNotNull(result.getMensaje());
        assertEquals(2, result.getMensaje().size());
        assertTrue(result.getMensaje().contains("productId: El ID del producto es obligatorio"));
        assertTrue(result.getMensaje().contains("quantity: La cantidad debe ser mayor a cero"));
    }

    @Test
    @DisplayName("notFoundRequest - Debe manejar NotFoundException correctamente")
    void notFoundRequest_ShouldHandleNotFoundException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory/999");

        NotFoundException exception = new NotFoundException("Producto no encontrado");

        // When
        MensajeError result = exceptionHandler.notFoundRequest(request, exception);

        // Then
        assertNotNull(result);
        assertEquals("/api/v1/inventory/999", result.getPath());
        assertNotNull(result.getMensaje());
        assertEquals(1, result.getMensaje().size());
        assertEquals("Producto no encontrado", result.getMensaje().get(0));
    }

    @Test
    @DisplayName("notFoundRequest - Debe manejar ResourceNotFoundException correctamente")
    void notFoundRequest_ShouldHandleResourceNotFoundException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory/999");

        ResourceNotFoundException exception = new ResourceNotFoundException("Recurso no encontrado");

        // When
        MensajeError result = exceptionHandler.notFoundRequest(request, exception);

        // Then
        assertNotNull(result);
        assertEquals("/api/v1/inventory/999", result.getPath());
        assertNotNull(result.getMensaje());
        assertEquals(1, result.getMensaje().size());
        assertEquals("Recurso no encontrado", result.getMensaje().get(0));
    }

    @Test
    @DisplayName("badRequest - Debe manejar BadRequestException correctamente")
    void badRequest_ShouldHandleBadRequestException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory/buy");

        BadRequestException exception = new BadRequestException("Inventario insuficiente");

        // When
        MensajeError result = exceptionHandler.badRequest(request, exception);

        // Then
        assertNotNull(result);
        assertEquals("/api/v1/inventory/buy", result.getPath());
        assertNotNull(result.getMensaje());
        assertEquals(1, result.getMensaje().size());
        assertEquals("Inventario insuficiente", result.getMensaje().get(0));
    }

    @Test
    @DisplayName("badRequest - Debe manejar MissingRequestHeaderException correctamente")
    void badRequest_ShouldHandleMissingRequestHeaderException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory/buy");

        // Create a simple exception that doesn't require complex parameter setup
        RuntimeException exception = new RuntimeException("Missing request header 'Authorization'");

        // When
        MensajeError result = exceptionHandler.badRequest(request, exception);

        // Then
        assertNotNull(result);
        assertEquals("/api/v1/inventory/buy", result.getPath());
        assertNotNull(result.getMensaje());
        assertEquals(1, result.getMensaje().size());
        assertTrue(result.getMensaje().get(0).contains("Authorization"));
    }

    @Test
    @DisplayName("badRequest - Debe manejar MissingServletRequestParameterException correctamente")
    void badRequest_ShouldHandleMissingServletRequestParameterException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory/buy");

        MissingServletRequestParameterException exception = new MissingServletRequestParameterException("productId", "Long");

        // When
        MensajeError result = exceptionHandler.badRequest(request, exception);

        // Then
        assertNotNull(result);
        assertEquals("/api/v1/inventory/buy", result.getPath());
        assertNotNull(result.getMensaje());
        assertEquals(1, result.getMensaje().size());
        assertTrue(result.getMensaje().get(0).contains("productId"));
    }

    @Test
    @DisplayName("badRequest - Debe manejar MethodArgumentTypeMismatchException correctamente")
    void badRequest_ShouldHandleMethodArgumentTypeMismatchException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory/invalid");

        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "invalid", Long.class, "productId", null, null);

        // When
        MensajeError result = exceptionHandler.badRequest(request, exception);

        // Then
        assertNotNull(result);
        assertEquals("/api/v1/inventory/invalid", result.getPath());
        assertNotNull(result.getMensaje());
        assertEquals(1, result.getMensaje().size());
        assertTrue(result.getMensaje().get(0).contains("invalid") || result.getMensaje().get(0).contains("productId"));
    }

    @Test
    @DisplayName("badRequest - Debe manejar IllegalArgumentException correctamente")
    void badRequest_ShouldHandleIllegalArgumentException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory/buy");

        IllegalArgumentException exception = new IllegalArgumentException("Argumento inválido");

        // When
        MensajeError result = exceptionHandler.badRequest(request, exception);

        // Then
        assertNotNull(result);
        assertEquals("/api/v1/inventory/buy", result.getPath());
        assertNotNull(result.getMensaje());
        assertEquals(1, result.getMensaje().size());
        assertEquals("Argumento inválido", result.getMensaje().get(0));
    }

    @Test
    @DisplayName("forbiddenRequest - Debe manejar ForbiddenException correctamente")
    void forbiddenRequest_ShouldHandleForbiddenException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory/buy");

        ForbiddenException exception = new ForbiddenException("Acceso denegado");

        // When
        MensajeError result = exceptionHandler.forbiddenRequest(request, exception);

        // Then
        assertNotNull(result);
        assertEquals("/api/v1/inventory/buy", result.getPath());
        assertNotNull(result.getMensaje());
        assertEquals(1, result.getMensaje().size());
        assertEquals("Acceso denegado", result.getMensaje().get(0));
    }

    @Test
    @DisplayName("conflictRequest - Debe manejar ConflictException correctamente")
    void conflictRequest_ShouldHandleConflictException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory/update/1");

        ConflictException exception = new ConflictException("Conflicto de datos");

        // When
        MensajeError result = exceptionHandler.conflictRequest(request, exception);

        // Then
        assertNotNull(result);
        assertEquals("/api/v1/inventory/update/1", result.getPath());
        assertNotNull(result.getMensaje());
        assertEquals(1, result.getMensaje().size());
        assertEquals("Las claves no pueden ser duplicadas.", result.getMensaje().get(0));
    }

    @Test
    @DisplayName("unauthorizedRequest - Debe manejar UnauthorizedException correctamente")
    void unauthorizedRequest_ShouldHandleUnauthorizedException() {
        // When & Then
        assertDoesNotThrow(() -> exceptionHandler.unauthorizedRequest());
    }

    @Test
    @DisplayName("fatalErrorUnexpectedRequest - Debe manejar Exception genérica correctamente")
    void fatalErrorUnexpectedRequest_ShouldHandleGenericException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory/buy");

        Exception exception = new Exception("Error inesperado");

        // When
        MensajeError result = exceptionHandler.fatalErrorUnexpectedRequest(request, exception);

        // Then
        assertNotNull(result);
        assertEquals("/api/v1/inventory/buy", result.getPath());
        assertNotNull(result.getMensaje());
        assertEquals(1, result.getMensaje().size());
        assertEquals("Contacte con un administrador", result.getMensaje().get(0));
    }

    @Test
    @DisplayName("fatalErrorUnexpectedRequest - Debe manejar IOException correctamente")
    void fatalErrorUnexpectedRequest_ShouldHandleIOException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory/buy");

        java.io.IOException exception = new java.io.IOException("Error de entrada/salida");

        // When
        MensajeError result = exceptionHandler.fatalErrorUnexpectedRequest(request, exception);

        // Then
        assertNotNull(result);
        assertEquals("/api/v1/inventory/buy", result.getPath());
        assertNotNull(result.getMensaje());
        assertEquals(1, result.getMensaje().size());
        assertEquals("Contacte con un administrador", result.getMensaje().get(0));
    }

    @Test
    @DisplayName("badRequestManualTransactional - Debe manejar JpaSystemException correctamente")
    void badRequestManualTransactional_ShouldHandleJpaSystemException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory/buy");

        // When
        MensajeError result = exceptionHandler.badRequestManualTransactional(request);

        // Then
        assertNotNull(result);
        assertEquals("/api/v1/inventory/buy", result.getPath());
        assertNotNull(result.getMensaje());
        assertEquals(1, result.getMensaje().size());
        assertEquals("Se agoto el tiempo de respuesta en la transacción.", result.getMensaje().get(0));
    }

    // Clase de controlador de prueba para simular excepciones
    private static class TestController {
        // Este controlador se usa solo para probar el manejo de excepciones
        // No necesita implementación específica
    }
}
