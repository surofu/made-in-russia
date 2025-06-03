package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.auth.operation.LoginWithLogin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LoginWithLoginMapperResultToResponseEntity implements LoginWithLogin.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(LoginWithLogin.Result.Success result) {
        return new ResponseEntity<>(result.getLoginSuccessDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processInvalidCredentials(LoginWithLogin.Result.InvalidCredentials result) {
        String message = "Неверный логин или пароль";
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.BAD_REQUEST);
    }
}
