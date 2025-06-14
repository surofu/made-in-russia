package com.surofu.madeinrussia.infrastructure.web.mapper.me;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.core.service.me.operation.UpdateMe;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UpdateMeMapperResultToResponseEntity implements UpdateMe.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(UpdateMe.Result.Success result) {
        return new ResponseEntity<>(result.getAccountDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processForbiddenForNewAccount(UpdateMe.Result.ForbiddenForNewAccount result) {
        String message = String.format("Нет доступа. Вы сможете изменить данные аккаунта после %s", result.getAccessDateTime());
        SimpleResponseMessageDto simpleResponseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(simpleResponseMessageDto, HttpStatus.FORBIDDEN);
    }
}
