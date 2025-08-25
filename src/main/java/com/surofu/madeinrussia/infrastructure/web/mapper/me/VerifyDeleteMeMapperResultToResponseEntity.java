package com.surofu.madeinrussia.infrastructure.web.mapper.me;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.me.operation.VerifyDeleteMe;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerifyDeleteMeMapperResultToResponseEntity
        implements VerifyDeleteMe.Result.Processor<ResponseEntity<?>> {
    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(VerifyDeleteMe.Result.Success result) {
        String message = localizationManager.localize("me.delete_account.success");
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processDeleteError(VerifyDeleteMe.Result.DeleteError result) {
        String message = localizationManager.localize("me.delete_account.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processConfirmationNotFound(VerifyDeleteMe.Result.ConfirmationNotFound result) {
        String message = localizationManager.localize("me.delete_account.confirmation.not_found");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processInvalidConfirmationCode(VerifyDeleteMe.Result.InvalidConfirmationCode result) {
        String message = localizationManager.localize("me.delete_account.invalid_confirmation_code");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
}
