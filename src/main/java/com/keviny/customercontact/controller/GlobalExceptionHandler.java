package com.keviny.customercontact.controller;

import com.keviny.customercontact.dto.ErrorResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Error(global = true)
    public HttpResponse<ErrorResponse> handleConstraintViolationException(HttpRequest request, ConstraintViolationException ex) {
        logger.warn("Constraint violation: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(fieldName, message);
        });
        
        ErrorResponse errorResponse = new ErrorResponse("Validation failed", errors);
        return HttpResponse.badRequest(errorResponse);
    }
    
    @Error(global = true)
    public HttpResponse<ErrorResponse> handleIllegalArgumentException(HttpRequest request, IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        errors.put("argument", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse("Invalid argument", errors);
        return HttpResponse.badRequest(errorResponse);
    }
    
    @Error(global = true)
    public HttpResponse<ErrorResponse> handleGenericException(HttpRequest request, Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "An unexpected error occurred. Please try again later.");
        
        ErrorResponse errorResponse = new ErrorResponse("Internal server error", errors);
        return HttpResponse.serverError(errorResponse);
    }
}