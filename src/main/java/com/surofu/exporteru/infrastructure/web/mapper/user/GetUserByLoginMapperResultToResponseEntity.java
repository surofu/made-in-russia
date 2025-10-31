package com.surofu.exporteru.infrastructure.web.mapper.user;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.user.operation.GetUserByLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetUserByLoginMapperResultToResponseEntity
        implements GetUserByLogin.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetUserByLogin.Result.Success result) {
        return new ResponseEntity<>(result.getDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetUserByLogin.Result.NotFound result) {
        String errorMessage = localizationManager.localize("user.not_found_by_login", result.getLogin().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(errorMessage, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
