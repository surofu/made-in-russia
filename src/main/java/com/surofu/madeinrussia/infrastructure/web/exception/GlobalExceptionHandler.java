package com.surofu.madeinrussia.infrastructure.web.exception;

import com.surofu.madeinrussia.application.dto.SimpleResponseErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public SimpleResponseErrorDto handleAllExceptions(Exception ex) {
//        log.error(ex.getMessage(), ex);
//        return SimpleResponseErrorDto.of("Произошла внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
