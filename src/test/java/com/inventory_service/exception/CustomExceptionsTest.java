package com.inventory_service.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para las excepciones personalizadas.
 * 
 * @author Diego Alexander Villalba
 * @since Octubre 2025
 */
@DisplayName("Custom Exceptions Tests")
class CustomExceptionsTest {

    @Test
    @DisplayName("NotFoundException - Debe crear excepción con mensaje personalizado")
    void notFoundException_ShouldCreateWithCustomMessage() {
        // Given
        String customMessage = "Producto no encontrado";

        // When
        NotFoundException exception = new NotFoundException(customMessage);

        // Then
        assertNotNull(exception);
        assertEquals(customMessage, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("NotFoundException - Debe crear excepción con mensaje nulo")
    void notFoundException_ShouldCreateWithNullMessage() {
        // When
        NotFoundException exception = new NotFoundException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("BadRequestException - Debe crear excepción con mensaje personalizado")
    void badRequestException_ShouldCreateWithCustomMessage() {
        // Given
        String customMessage = "Inventario insuficiente";

        // When
        BadRequestException exception = new BadRequestException(customMessage);

        // Then
        assertNotNull(exception);
        assertEquals(customMessage, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("BadRequestException - Debe crear excepción con mensaje nulo")
    void badRequestException_ShouldCreateWithNullMessage() {
        // When
        BadRequestException exception = new BadRequestException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("ConflictException - Debe crear excepción con mensaje personalizado")
    void conflictException_ShouldCreateWithCustomMessage() {
        // Given
        String customMessage = "Conflicto de datos";

        // When
        ConflictException exception = new ConflictException(customMessage);

        // Then
        assertNotNull(exception);
        assertEquals(customMessage, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("ForbiddenException - Debe crear excepción con mensaje personalizado")
    void forbiddenException_ShouldCreateWithCustomMessage() {
        // Given
        String customMessage = "Acceso denegado";

        // When
        ForbiddenException exception = new ForbiddenException(customMessage);

        // Then
        assertNotNull(exception);
        assertEquals(customMessage, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("UnauthorizedException - Debe crear excepción con mensaje personalizado")
    void unauthorizedException_ShouldCreateWithCustomMessage() {
        // Given
        String customMessage = "No autorizado";

        // When
        UnauthorizedException exception = new UnauthorizedException(customMessage);

        // Then
        assertNotNull(exception);
        assertEquals(customMessage, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("InternalServerErrorException - Debe crear excepción con mensaje personalizado")
    void internalServerErrorException_ShouldCreateWithCustomMessage() {
        // Given
        String customMessage = "Error interno del servidor";

        // When
        InternalServerErrorException exception = new InternalServerErrorException(customMessage);

        // Then
        assertNotNull(exception);
        assertEquals(customMessage, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("ResourceNotFoundException - Debe crear excepción con mensaje personalizado")
    void resourceNotFoundException_ShouldCreateWithCustomMessage() {
        // Given
        String customMessage = "Recurso no encontrado";

        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(customMessage);

        // Then
        assertNotNull(exception);
        assertEquals(customMessage, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("MensajeError - Debe crear objeto con mensaje y path")
    void mensajeError_ShouldCreateWithMessageAndPath() {
        // Given
        String path = "/api/v1/inventory/buy";
        String message = "Error de validación";

        // When
        MensajeError mensajeError = new MensajeError(List.of(message), path);

        // Then
        assertNotNull(mensajeError);
        assertEquals(path, mensajeError.getPath());
        assertNotNull(mensajeError.getMensaje());
        assertEquals(1, mensajeError.getMensaje().size());
        assertEquals(message, mensajeError.getMensaje().get(0));
    }

    @Test
    @DisplayName("MensajeError - Debe crear objeto con constructor por defecto")
    void mensajeError_ShouldCreateWithDefaultConstructor() {
        // When
        MensajeError mensajeError = new MensajeError();

        // Then
        assertNotNull(mensajeError);
        assertNull(mensajeError.getPath());
        assertNull(mensajeError.getMensaje());
    }

    @Test
    @DisplayName("MensajeError - Debe permitir establecer path y mensaje")
    void mensajeError_ShouldAllowSettingPathAndMessage() {
        // Given
        String path = "/api/v1/inventory/buy";
        String message = "Error de validación";

        // When
        MensajeError mensajeError = new MensajeError();
        mensajeError.setPath(path);
        mensajeError.setMensaje(List.of(message));

        // Then
        assertEquals(path, mensajeError.getPath());
        assertNotNull(mensajeError.getMensaje());
        assertEquals(1, mensajeError.getMensaje().size());
        assertEquals(message, mensajeError.getMensaje().get(0));
    }

    @Test
    @DisplayName("MensajeError - Debe manejar múltiples mensajes")
    void mensajeError_ShouldHandleMultipleMessages() {
        // Given
        String path = "/api/v1/inventory/buy";
        List<String> messages = List.of("Error 1", "Error 2", "Error 3");

        // When
        MensajeError mensajeError = new MensajeError(messages, path);

        // Then
        assertNotNull(mensajeError);
        assertEquals(path, mensajeError.getPath());
        assertNotNull(mensajeError.getMensaje());
        assertEquals(3, mensajeError.getMensaje().size());
        assertEquals("Error 1", mensajeError.getMensaje().get(0));
        assertEquals("Error 2", mensajeError.getMensaje().get(1));
        assertEquals("Error 3", mensajeError.getMensaje().get(2));
    }
}
