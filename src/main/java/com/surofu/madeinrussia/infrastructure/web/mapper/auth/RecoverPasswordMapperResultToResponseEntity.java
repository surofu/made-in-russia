package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.auth.operation.RecoverPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecoverPasswordMapperResultToResponseEntity
implements RecoverPassword.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(RecoverPassword.Result.Success result) {
        String message = localizationManager.localize("auth.reset_password.verification_code_send_to_email", result.getUserEmail());
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processUserNotFound(RecoverPassword.Result.UserNotFound result) {
        String message = localizationManager.localize("auth.reset_password.user_with_email_not_found", result.getUserEmail());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
