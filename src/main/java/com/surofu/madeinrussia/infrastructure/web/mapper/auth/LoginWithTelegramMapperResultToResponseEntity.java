package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.core.service.auth.operation.LoginWithTelegram;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LoginWithTelegramMapperResultToResponseEntity
implements LoginWithTelegram.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(LoginWithTelegram.Result.Success result) {
        return new ResponseEntity<>(result.getDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processFailure(LoginWithTelegram.Result.Failure result) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
