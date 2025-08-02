package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.auth.operation.ForceRegister;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForceRegisterMapperResultToResponseEntity
        implements ForceRegister.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(ForceRegister.Result.Success result) {
        String message = localizationManager.localize("auth.register.success");
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processUserWithEmailAlreadyExists(ForceRegister.Result.UserWithEmailAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_email_already_exists", result.getEmail().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processUserWithLoginAlreadyExists(ForceRegister.Result.UserWithLoginAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_login_already_exists", result.getLogin().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processUserWithPhoneNumberAlreadyExists(ForceRegister.Result.UserWithPhoneNumberAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_phone_already_exists", result.getPhoneNumber().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processSaveError(ForceRegister.Result.SaveError result) {
        String message = localizationManager.localize("auth.register.save_error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
