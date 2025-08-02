package com.surofu.madeinrussia.infrastructure.web.mapper.user;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.user.operation.ForceUpdateUserById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForceUpdateUserByIdMapperResultToResponseEntity
        implements ForceUpdateUserById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(ForceUpdateUserById.Result.Success result) {
        String message = localizationManager.localize("user.save.success");
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(ForceUpdateUserById.Result.NotFound result) {
        String message = localizationManager.localize("user.not_found_by_id", result.getId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processEmailAlreadyExists(ForceUpdateUserById.Result.EmailAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_email_already_exists", result.getEmail().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processLoginAlreadyExists(ForceUpdateUserById.Result.LoginAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_login_already_exists", result.getLogin().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processPhoneNumberAlreadyExists(ForceUpdateUserById.Result.PhoneNumberAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_phone_already_exists", result.getPhoneNumber().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processSaveError(ForceUpdateUserById.Result.SaveError result) {
        String message = localizationManager.localize("user.save.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
