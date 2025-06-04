package com.surofu.madeinrussia.infrastructure.web.exception;

import com.surofu.madeinrussia.application.dto.ValidationExceptionDto;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Hidden
@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationExceptionDto> handleConstraintViolation(ConstraintViolationException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getConstraintViolations().forEach(violation -> {
            String fieldName = extractFieldName(violation);
            String message = violation.getMessage();
            errors.put(fieldName, message);
        });

        ValidationExceptionDto validationExceptionDto = new ValidationExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errors,
                "Validation failed"
        );

        return new ResponseEntity<>(validationExceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, Map<String, String>> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put("errors", Map.of(error.getField(), Objects.requireNonNull(error.getDefaultMessage())))
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ValidationExceptionDto> handleIllegalArgumentException(IllegalArgumentException exception) {
        Map<String, String> errors = new HashMap<>();

        errors.put("message", exception.getMessage());

        ValidationExceptionDto validationExceptionDto = new ValidationExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errors,
                "Validation failed"
        );

        return new ResponseEntity<>(validationExceptionDto, HttpStatus.BAD_REQUEST);
    }

    private String extractFieldName(ConstraintViolation<?> violation) {
        String fullPath = violation.getPropertyPath().toString();
        return fullPath.substring(fullPath.lastIndexOf('.') + 1);
    }
}
