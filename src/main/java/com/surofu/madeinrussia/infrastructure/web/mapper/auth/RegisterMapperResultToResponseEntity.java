package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.auth.operation.Register;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterMapperResultToResponseEntity
        implements Register.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(Register.Result.Success result) {
        String message = localizationManager.localize("auth.register.verification_code_sand_to_email", result.getUserEmail().toString());
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processUserWithEmailAlreadyExists(Register.Result.UserWithEmailAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_email_already_exists", result.getUserEmail().toString());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processUserWithLoginAlreadyExists(Register.Result.UserWithLoginAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_login_already_exists", result.getUserLogin().toString());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processUserWithPhoneNumberAlreadyExists(Register.Result.UserWithPhoneNumberAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_phone_already_exists", result.getUserPhoneNumber().toString());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.CONFLICT);
    }
}
