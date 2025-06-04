package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.auth.operation.RegisterVendor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RegisterVendorMapperResultToResponseEntity implements RegisterVendor.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(RegisterVendor.Result.Success result) {
        return new ResponseEntity<>(result.getResponseMessageDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processUserWithEmailAlreadyExists(RegisterVendor.Result.UserWithEmailAlreadyExists result) {
        String message = String.format("Пользователь с почтой '%s' уже существует", result.getUserEmail());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processUserWithLoginAlreadyExists(RegisterVendor.Result.UserWithLoginAlreadyExists result) {
        String message = String.format("Пользователь с логином '%s' уже существует", result.getUserLogin());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processUserWithPhoneNumberAlreadyExists(RegisterVendor.Result.UserWithPhoneNumberAlreadyExists result) {
        String message = String.format("Пользователь с телефоном '%s' уже существует", result.getUserPhoneNumber());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.CONFLICT);
    }
}
