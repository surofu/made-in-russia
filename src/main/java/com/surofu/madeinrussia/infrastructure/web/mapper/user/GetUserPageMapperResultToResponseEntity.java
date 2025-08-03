package com.surofu.madeinrussia.infrastructure.web.mapper.user;

import com.surofu.madeinrussia.core.service.user.operation.GetUserPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetUserPageMapperResultToResponseEntity
        implements GetUserPage.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetUserPage.Result.Success result) {
        return new ResponseEntity<>(result.getPage(), HttpStatus.OK);
    }
}
