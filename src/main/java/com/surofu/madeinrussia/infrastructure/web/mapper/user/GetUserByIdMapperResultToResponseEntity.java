package com.surofu.madeinrussia.infrastructure.web.mapper.user;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.user.operation.GetUserById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetUserByIdMapperResultToResponseEntity
        implements GetUserById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetUserById.Result.Success result) {
        return new ResponseEntity<>(result.getUserDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetUserById.Result.NotFound result) {
        String message = localizationManager.localize("user.not_found_by_id", result.getUserId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
