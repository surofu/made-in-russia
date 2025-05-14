package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.core.service.auth.operation.Logout;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LogoutMapperResultToResponseEntity implements Logout.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(Logout.Result.Success result) {
        return new ResponseEntity<>(result.getSimpleResponseMessageDto(), HttpStatus.OK);
    }
}
