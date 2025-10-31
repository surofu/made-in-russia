package com.surofu.exporteru.infrastructure.web.mapper.me;

import com.surofu.exporteru.core.service.me.operation.GetMeSessions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMeSessionsResultToResponseEntity implements GetMeSessions.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetMeSessions.Result.Success result) {
        return new ResponseEntity<>(result.getSessionDtos(), HttpStatus.OK);
    }
}
