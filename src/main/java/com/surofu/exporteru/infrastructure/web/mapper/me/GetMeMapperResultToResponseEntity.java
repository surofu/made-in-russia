package com.surofu.exporteru.infrastructure.web.mapper.me;

import com.surofu.exporteru.core.service.me.operation.GetMe;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMeMapperResultToResponseEntity implements GetMe.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetMe.Result.Success result) {
        return new ResponseEntity<>(result.getAbstractAccountDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processSessionWithIdNotFound(GetMe.Result.SessionWithIdNotFound result) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> processSessionWithUserIdAndDeviceIdNotFound(GetMe.Result.SessionWithUserIdAndDeviceIdNotFound result) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
