package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.auth.operation.LoginWithEmail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LoginWithEmailMapperResultToResponseEntity implements LoginWithEmail.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(LoginWithEmail.Result.Success result) {
        return new ResponseEntity<>(result.getLoginSuccessDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processInvalidCredentials(LoginWithEmail.Result.InvalidCredentials result) {
        String message = "Неверная почта или пароль";
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processNotVerified(LoginWithEmail.Result.NotVerified result) {
        String message = "Аккаунт не верифицирован, сначала подтвердите почту";
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.BAD_REQUEST);
    }
}
