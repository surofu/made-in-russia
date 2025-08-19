package com.surofu.madeinrussia.application.exception;

import com.surofu.madeinrussia.application.dto.error.ValidationExceptionDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Hidden
@RestControllerAdvice
@RequiredArgsConstructor
public class LocalizedExceptionHandler {

    private final LocalizationManager localizationManager;

    @ExceptionHandler(LocalizedValidationException.class)
    public ResponseEntity<ValidationExceptionDto> handleValidationException(LocalizedValidationException exception) {
        String message = localizationManager.localize(exception.getMessage(), exception.getValues());
        Map<String, String> errors = Collections.singletonMap("message", message);
        ValidationExceptionDto dto = new ValidationExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errors,
                "Validation failed"
        );
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ValidationExceptionDto> handleInvalidRoleException(InvalidRoleException exception) {
        Map<String, String> errors = new HashMap<>();
        String message = localizationManager.localize("validation.role.not_found", exception.getMessage());
        errors.put("message", message);
        ValidationExceptionDto dto = new ValidationExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errors,
                "Validation failed"
        );
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }
}
