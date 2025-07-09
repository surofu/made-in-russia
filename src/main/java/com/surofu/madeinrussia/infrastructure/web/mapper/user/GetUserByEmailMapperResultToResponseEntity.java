package com.surofu.madeinrussia.infrastructure.web.mapper.user;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.user.operation.GetUserByEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetUserByEmailMapperResultToResponseEntity
        implements GetUserByEmail.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetUserByEmail.Result.Success result) {
        return new ResponseEntity<>(result.getUserDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetUserByEmail.Result.NotFound result) {
        String errorMessage = localizationManager.localize("user.not_found_by_email", result.getUserEmail().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(errorMessage, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
