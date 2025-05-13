package com.surofu.madeinrussia.infrastructure.web.mapper.me;

import com.surofu.madeinrussia.core.service.me.operation.GetMeByJwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMeByJwtMapperResultToResponseEntity implements GetMeByJwt.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetMeByJwt.Result.Success result) {
        return new ResponseEntity<>(result.getUserDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processSessionWithDeviceNotFound(GetMeByJwt.Result.SessionWithDeviceNotFound result) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
