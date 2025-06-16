package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.auth.operation.RecoverPassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RecoverPasswordMapperResultToResponseEntity
implements RecoverPassword.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(RecoverPassword.Result.Success result) {
        String message = String.format("Код для восстановления пароля был отправлен на почту %s", result.getUserEmail());
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processUserNotFound(RecoverPassword.Result.UserNotFound result) {
        String message = String.format("Пользователь с почтой '%s' не найден", result.getUserEmail());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
