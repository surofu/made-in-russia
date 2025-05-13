package com.surofu.madeinrussia.infrastructure.web.mapper.me;

import com.surofu.madeinrussia.core.service.me.operation.GetMe;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMeMapperResultToResponseEntity implements GetMe.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetMe.Result.Success result) {
        return new ResponseEntity<>(result.getUserDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processSessionIsEmpty(GetMe.Result.SessionIsEmpty result) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
