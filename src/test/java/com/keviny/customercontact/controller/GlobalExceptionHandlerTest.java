package com.keviny.customercontact.controller;

import com.keviny.customercontact.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequest() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("contactDto", "firstName", "First name is required");
        FieldError fieldError2 = new FieldError("contactDto", "email", "Email should be valid");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(java.util.Arrays.asList(fieldError1, fieldError2));

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().containsKey("firstName"));
        assertTrue(response.getBody().getDetails().containsKey("email"));
        assertEquals("First name is required", response.getBody().getDetails().get("firstName"));
        assertEquals("Email should be valid", response.getBody().getDetails().get("email"));
    }

    @Test
    void handleConstraintViolationException_ShouldReturnBadRequest() {
        // Given
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        Path path2 = mock(Path.class);
        
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("ID must be positive");
        when(path1.toString()).thenReturn("id");
        
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage()).thenReturn("Email format is invalid");
        when(path2.toString()).thenReturn("email");
        
        when(exception.getConstraintViolations()).thenReturn(Set.of(violation1, violation2));

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolationException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().containsKey("id"));
        assertTrue(response.getBody().getDetails().containsKey("email"));
        assertEquals("ID must be positive", response.getBody().getDetails().get("id"));
        assertEquals("Email format is invalid", response.getBody().getDetails().get("email"));
    }

    @Test
    void handleTypeMismatchException_ShouldReturnBadRequest_WithValidData() {
        // Given
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("id");
        when(exception.getValue()).thenReturn("invalid_id");
        when(exception.getRequiredType()).thenReturn((Class) Long.class);

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTypeMismatchException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid parameter type", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().containsKey("id"));
        assertEquals("Invalid value: invalid_id. Expected type: Long", response.getBody().getDetails().get("id"));
    }

    @Test
    void handleTypeMismatchException_ShouldHandleNullRequiredType() {
        // Given
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("id");
        when(exception.getValue()).thenReturn("invalid_id");
        when(exception.getRequiredType()).thenReturn(null); // Testing our null safety fix

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTypeMismatchException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid parameter type", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().containsKey("id"));
        assertEquals("Invalid value: invalid_id. Expected type: Unknown", response.getBody().getDetails().get("id"));
    }

    @Test
    void handleTypeMismatchException_ShouldHandleNullParameterName() {
        // Given
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn(null); // Testing our null safety fix
        when(exception.getValue()).thenReturn("invalid_value");
        when(exception.getRequiredType()).thenReturn((Class) String.class);

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTypeMismatchException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid parameter type", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().containsKey("parameter"));
        assertEquals("Invalid value: invalid_value. Expected type: String", response.getBody().getDetails().get("parameter"));
    }

    @Test
    void handleTypeMismatchException_ShouldHandleNullValue() {
        // Given
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("id");
        when(exception.getValue()).thenReturn(null); // Testing our null safety fix
        when(exception.getRequiredType()).thenReturn((Class) Long.class);

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTypeMismatchException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid parameter type", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().containsKey("id"));
        assertEquals("Invalid value: null. Expected type: Long", response.getBody().getDetails().get("id"));
    }

    @Test
    void handleHttpMessageNotReadableException_ShouldReturnBadRequest() {
        // Given
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getMessage()).thenReturn("Malformed JSON");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadableException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid request format", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().containsKey("json"));
        assertEquals("Malformed JSON request body", response.getBody().getDetails().get("json"));
    }

    @Test
    void handleMethodNotSupportedException_ShouldReturnMethodNotAllowed() {
        // Given
        HttpRequestMethodNotSupportedException exception = mock(HttpRequestMethodNotSupportedException.class);
        when(exception.getMethod()).thenReturn("DELETE");
        when(exception.getMessage()).thenReturn("DELETE not supported");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMethodNotSupportedException(exception);

        // Then
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Method not allowed", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().containsKey("method"));
        assertEquals("HTTP method DELETE is not supported for this endpoint", response.getBody().getDetails().get("method"));
    }

    @Test
    void handleDataIntegrityViolationException_ShouldReturnConflict() {
        // Given
        DataIntegrityViolationException exception = mock(DataIntegrityViolationException.class);
        when(exception.getMessage()).thenReturn("Duplicate entry");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolationException(exception);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Data conflict", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().containsKey("database"));
        assertEquals("Data integrity violation - duplicate or invalid data", response.getBody().getDetails().get("database"));
    }

    @Test
    void handleDataAccessException_ShouldReturnServiceUnavailable() {
        // Given
        DataAccessException exception = new DataAccessException("Database connection failed") {};

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataAccessException(exception);

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Database error", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().containsKey("database"));
        assertEquals("Database access error - please try again later", response.getBody().getDetails().get("database"));
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid email format");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid argument", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().containsKey("argument"));
        assertEquals("Invalid email format", response.getBody().getDetails().get("argument"));
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        // Given
        Exception exception = new RuntimeException("Unexpected error occurred");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().containsKey("error"));
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getDetails().get("error"));
    }

    @Test
    void errorResponse_ShouldHaveTimestamp() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Test error");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Then
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
    }
}