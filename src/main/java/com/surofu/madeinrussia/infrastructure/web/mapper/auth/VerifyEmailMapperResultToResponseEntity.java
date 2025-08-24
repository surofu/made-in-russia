package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.auth.operation.VerifyEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerifyEmailMapperResultToResponseEntity
        implements VerifyEmail.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(VerifyEmail.Result.Success result) {
        return new ResponseEntity<>(result.getVerifyEmailSuccessDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processAccountNotFound(VerifyEmail.Result.AccountNotFound result) {
        String message = localizationManager.localize("auth.email_verification.email_not_found", result.getUserEmail().toString());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processInvalidVerificationCode(VerifyEmail.Result.InvalidVerificationCode result) {
        String message = localizationManager.localize("auth.email_verification.invalid_code");
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processTranslationError(VerifyEmail.Result.TranslationError result) {
        String message = localizationManager.localize("auth.email_verification.translation_error");
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processSaveError(VerifyEmail.Result.SaveError result) {
        String message = localizationManager.localize("auth.register.save_error");
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processSaveSessionError(VerifyEmail.Result.SaveSessionError result) {
        String message = localizationManager.localize("auth.register.save_error");
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
