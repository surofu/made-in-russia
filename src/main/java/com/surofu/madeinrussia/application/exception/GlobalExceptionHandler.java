package com.surofu.madeinrussia.application.exception;

import com.surofu.madeinrussia.application.dto.error.InternalServerErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LocalizationManager localizationManager;

    @ExceptionHandler({
            // Spring Framework
            org.springframework.beans.factory.BeanCreationException.class,
            org.springframework.beans.factory.UnsatisfiedDependencyException.class,
            org.springframework.dao.DataAccessException.class,
            org.springframework.transaction.TransactionException.class,
            org.springframework.web.HttpRequestMethodNotSupportedException.class,
            org.springframework.web.bind.MissingServletRequestParameterException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class,
            org.springframework.validation.BindException.class,

            // JPA/Hibernate
            jakarta.persistence.EntityNotFoundException.class,
            jakarta.persistence.NoResultException.class,
            jakarta.persistence.NonUniqueResultException.class,
            jakarta.persistence.OptimisticLockException.class,
            jakarta.persistence.PersistenceException.class,
            org.hibernate.LazyInitializationException.class,
            org.hibernate.HibernateException.class,
            org.hibernate.exception.ConstraintViolationException.class,
            org.hibernate.exception.SQLGrammarException.class,

            // Undertow
            io.undertow.server.RequestTooBigException.class,
    })
    public ResponseEntity<InternalServerErrorDto> handleValidationException(Exception exception) {
        String message = localizationManager.localize("internal_server_error.unknown");

        int httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String reason = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();

        InternalServerErrorDto dto = new InternalServerErrorDto(
                httpStatus,
                reason,
                message,
                exception.getClass().getName(),
                exception.getMessage(),
                exception.getStackTrace()[0].toString()
        );
        return new ResponseEntity<>(dto, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
