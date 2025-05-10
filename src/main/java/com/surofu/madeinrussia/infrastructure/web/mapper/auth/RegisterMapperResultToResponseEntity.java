package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.auth.operation.Register;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RegisterMapperResultToResponseEntity implements Register.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(Register.Result.Success result) {
        return new ResponseEntity<>(result.getResponseMessageDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processUserWithEmailAlreadyExists(Register.Result.UserWithEmailAlreadyExists result) {
        String message = String.format("Пользователь с почтой '%s' уже существует", result.getEmail());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processUserWithLoginAlreadyExists(Register.Result.UserWithLoginAlreadyExists result) {
        String message = String.format("Пользователь с логином '%s' уже существует", result.getLogin());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.CONFLICT);
    }
}
