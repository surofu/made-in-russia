package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.auth.operation.LoginWithLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginWithLoginMapperResultToResponseEntity
        implements LoginWithLogin.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(LoginWithLogin.Result.Success result) {
        return new ResponseEntity<>(result.getLoginSuccessDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processInvalidCredentials(LoginWithLogin.Result.InvalidCredentials result) {
        String message = localizationManager.localize("auth.invalid_login_or_password");
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processAccountBlocked(LoginWithLogin.Result.AccountBlocked result) {
        String message = localizationManager.localize("auth.account_blocked");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }
}
