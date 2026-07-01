package com.surofu.exporteru.application.exception;

import com.surofu.exporteru.application.dto.error.InternalServerErrorDto;
import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

@Slf4j
@Hidden
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final LocalizationManager localizationManager;

    @ExceptionHandler({
        AsyncRequestNotUsableException.class,
        org.apache.catalina.connector.ClientAbortException.class
    })
    public void handleClientAbort(Exception exception) {
        log.debug("Клиент прервал соединение: {}", exception.getMessage());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<SimpleResponseErrorDto> handleAuthorizationDenied(AuthorizationDeniedException exception) {
        log.debug("Доступ запрещён: {}", exception.getMessage());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(exception.getMessage(), HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
        org.springframework.beans.factory.BeanCreationException.class,
        org.springframework.beans.factory.UnsatisfiedDependencyException.class,
        org.springframework.dao.DataAccessException.class,
        org.springframework.transaction.TransactionException.class,
        org.springframework.web.bind.MissingServletRequestParameterException.class,
        org.springframework.validation.BindException.class,
        jakarta.persistence.EntityNotFoundException.class,
        jakarta.persistence.NoResultException.class,
        jakarta.persistence.NonUniqueResultException.class,
        jakarta.persistence.OptimisticLockException.class,
        jakarta.persistence.PersistenceException.class,
        org.hibernate.LazyInitializationException.class,
        org.hibernate.HibernateException.class,
        org.hibernate.exception.ConstraintViolationException.class,
        org.hibernate.exception.SQLGrammarException.class
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
        log.error(message, exception);
        return new ResponseEntity<>(dto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
        org.springframework.http.converter.HttpMessageNotReadableException.class,
        org.springframework.web.HttpRequestMethodNotSupportedException.class,
    })
    public ResponseEntity<?> handleBadRequestException(Exception exception) {
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception exception) throws Exception {
        log.error("Неизвестная ошибка: {}", exception.getMessage(), exception);
        throw exception;
    }
}