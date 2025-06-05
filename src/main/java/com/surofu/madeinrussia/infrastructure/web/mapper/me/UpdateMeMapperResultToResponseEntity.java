package com.surofu.madeinrussia.infrastructure.web.mapper.me;

import com.surofu.madeinrussia.core.service.me.operation.UpdateMe;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UpdateMeMapperResultToResponseEntity implements UpdateMe.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(UpdateMe.Result.Success result) {
        return new ResponseEntity<>(result.getUserDto(), HttpStatus.OK);
    }
}
