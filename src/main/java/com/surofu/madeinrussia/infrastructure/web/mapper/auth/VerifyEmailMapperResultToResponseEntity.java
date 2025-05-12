package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.auth.operation.VerifyEmail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class VerifyEmailMapperResultToResponseEntity implements VerifyEmail.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(VerifyEmail.Result.Success result) {
        return new ResponseEntity<>(result.getVerifyEmailSuccessDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processAccountNotFound(VerifyEmail.Result.AccountNotFound result) {
        String message = "Аккаунт для подтверждения не найден";
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processInvalidVerificationCode(VerifyEmail.Result.InvalidVerificationCode result) {
        String message = "Неверный код подтверждения почты";
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processCacheNotFound(VerifyEmail.Result.CacheNotFound result) {
        String message = "Ошибка на стороне сервера";
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
