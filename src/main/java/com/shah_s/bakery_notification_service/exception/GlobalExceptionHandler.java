package com.shah_s.bakery_notification_service.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.devofblue.common.exception.ErrorResponseDto;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotificationServiceException.class)
    public ResponseEntity<ErrorResponseDto> handleNotificationServiceException(
            NotificationServiceException ex, WebRequest request) {

        logger.error("Notification service error: {}", ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
            "NOTIFICATION_SERVICE_ERROR",
            ex.getMessage(),
            LocalDateTime.now(),
            request.getDescription(false),
            null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {

        logger.error("Constraint violation error: {}", ex.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            validationErrors.put(fieldName, errorMessage);
        }

        ErrorResponseDto errorResponse = new ErrorResponseDto(
            "CONSTRAINT_VIOLATION",
            "Constraint violation in request data",
            LocalDateTime.now(),
            request.getDescription(false),
            validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {

        logger.error("HTTP message not readable: {}", ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
            "MALFORMED_REQUEST",
            "Malformed JSON request",
            LocalDateTime.now(),
            request.getDescription(false),
            null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        logger.error("Method argument type mismatch: {}", ex.getMessage());

        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                                     ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
            "INVALID_PARAMETER_TYPE",
            message,
            LocalDateTime.now(),
            request.getDescription(false),
            null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, WebRequest request) {

        logger.error("Missing request parameter: {}", ex.getMessage());

        String message = String.format("Missing required parameter: %s", ex.getParameterName());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
            "MISSING_PARAMETER",
            message,
            LocalDateTime.now(),
            request.getDescription(false),
            null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        logger.error("Access denied: {}", ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
            "ACCESS_DENIED",
            "Access denied - insufficient permissions",
            LocalDateTime.now(),
            request.getDescription(false),
            null
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
}
