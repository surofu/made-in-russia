package com.surofu.madeinrussia.infrastructure.web.mapper.user;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.user.operation.GetUserByLogin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetUserByLoginMapperResultToResponseEntity implements GetUserByLogin.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetUserByLogin.Result.Success result) {
        return new ResponseEntity<>(result.getUserDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetUserByLogin.Result.NotFound result) {
        String errorMessage = String.format("Пользователь с логином '%s' не найден", result.getUserLogin());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(errorMessage, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
