package com.surofu.madeinrussia.infrastructure.web.mapper.user;

import com.surofu.madeinrussia.application.dto.error.UserNotFoundByIdResponseErrorDto;
import com.surofu.madeinrussia.core.service.user.operation.GetUserById;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetUserByIdMapperResultToResponseEntity implements GetUserById.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetUserById.Result.Success result) {
        return new ResponseEntity<>(result.getUserDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetUserById.Result.NotFound result) {
        UserNotFoundByIdResponseErrorDto errorDto = UserNotFoundByIdResponseErrorDto.of(result.getUserId());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
