package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.auth.operation.VerifyRecoverPassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class VerifyRecoverPasswordMapperResultToResponseEntity
implements VerifyRecoverPassword.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(VerifyRecoverPassword.Result.Success result) {
        return new ResponseEntity<>(result.getRecoverPasswordSuccessDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processEmilNotFound(VerifyRecoverPassword.Result.EmailNotFound result) {
        String message = String.format("Запрос на восстановление по почте '%s' не найден", result.getUserEmail());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processInvalidRecoverCode(VerifyRecoverPassword.Result.InvalidRecoverCode result) {
        String message = "Неверный код восстановления пароля";
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processUserNotFound(VerifyRecoverPassword.Result.UserNotFound result) {
        String message = String.format("Аккаунт с почтой '%s' не найден", result.getUserEmail());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processAuthenticationFailed(VerifyRecoverPassword.Result.AuthenticationFailed result) {
        String message = "Не удалось авторизовать пользователя";
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
