package com.surofu.madeinrussia.infrastructure.web.mapper.me;

import com.surofu.madeinrussia.core.service.me.operation.GetMeCurrentSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMeCurrentSessionMapperResultToResponseEntity implements GetMeCurrentSession.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetMeCurrentSession.Result.Success result) {
        return new ResponseEntity<>(result.getSessionDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processSessionWithDeviceNotFound(GetMeCurrentSession.Result.SessionIsEmpty result) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
