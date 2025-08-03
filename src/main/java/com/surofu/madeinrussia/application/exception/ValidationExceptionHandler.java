package com.surofu.madeinrussia.application.exception;

import com.surofu.madeinrussia.application.dto.error.ValidationExceptionDto;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

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
                errors.put("errors", Map.of(error.getField(), Objects.requireNonNullElse(error.getDefaultMessage(), "empty error")))
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

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ValidationExceptionDto> handleMissingServletRequestPartException(MissingServletRequestPartException exception) {
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

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ValidationExceptionDto> handlePropertyReferenceException(PropertyReferenceException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", exception.getLocalizedMessage());
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
