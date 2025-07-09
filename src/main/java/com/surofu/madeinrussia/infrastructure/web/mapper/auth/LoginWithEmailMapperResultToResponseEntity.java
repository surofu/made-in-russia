package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.auth.operation.LoginWithEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginWithEmailMapperResultToResponseEntity
        implements LoginWithEmail.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(LoginWithEmail.Result.Success result) {
        return new ResponseEntity<>(result.getLoginSuccessDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processInvalidCredentials(LoginWithEmail.Result.InvalidCredentials result) {
        String message = localizationManager.localize("auth.invalid_email_or_password");
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.BAD_REQUEST);
    }
}
