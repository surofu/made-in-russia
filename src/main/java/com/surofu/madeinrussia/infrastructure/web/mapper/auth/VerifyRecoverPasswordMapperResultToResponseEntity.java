package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.auth.operation.VerifyRecoverPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerifyRecoverPasswordMapperResultToResponseEntity
implements VerifyRecoverPassword.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(VerifyRecoverPassword.Result.Success result) {
        return new ResponseEntity<>(result.getRecoverPasswordSuccessDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processEmilNotFound(VerifyRecoverPassword.Result.EmailNotFound result) {
        String message = localizationManager.localize("auth.verification_reset_password.email_not_found", result.getUserEmail().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processInvalidRecoverCode(VerifyRecoverPassword.Result.InvalidRecoverCode result) {
        String message = localizationManager.localize("auth.verification_reset_password.invalid_code");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processUserNotFound(VerifyRecoverPassword.Result.UserNotFound result) {
        String message = localizationManager.localize("auth.verification_reset_password.account_with_email_not_found", result.getUserEmail().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
