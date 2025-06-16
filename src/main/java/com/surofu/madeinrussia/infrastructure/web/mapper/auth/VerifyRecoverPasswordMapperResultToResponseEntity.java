package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.auth.operation.RecoverPassword;
import com.surofu.madeinrussia.core.service.auth.operation.VerifyRecoverPassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class VerifyRecoverPasswordMapperResultToResponseEntity
implements VerifyRecoverPassword.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(VerifyRecoverPassword.Result.Success result) {
        String message = "Пароль был успешно изменен";
        SimpleResponseMessageDto responseMessage = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
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
}
