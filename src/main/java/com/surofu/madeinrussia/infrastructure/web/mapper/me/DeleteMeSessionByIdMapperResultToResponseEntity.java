package com.surofu.madeinrussia.infrastructure.web.mapper.me;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.me.operation.DeleteMeSessionById;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class DeleteMeSessionByIdMapperResultToResponseEntity
implements DeleteMeSessionById.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(DeleteMeSessionById.Result.Success result) {
        String message = String.format("Сессия с ID '%s' была успешно удалена", result.getSessionId());
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(DeleteMeSessionById.Result.NotFound result) {
        String message = String.format("Сессия с ID '%s' не найдена", result.getSessionId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
