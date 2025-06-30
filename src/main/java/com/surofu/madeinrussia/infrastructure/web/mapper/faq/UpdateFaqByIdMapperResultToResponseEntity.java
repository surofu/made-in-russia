package com.surofu.madeinrussia.infrastructure.web.mapper.faq;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.faq.operation.UpdateFaqById;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UpdateFaqByIdMapperResultToResponseEntity
        implements UpdateFaqById.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(UpdateFaqById.Result.Success result) {
        String message = String.format("Вопрос/Ответ с ID '%s' был успешно изменен", result.getFaqId());
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(UpdateFaqById.Result.NotFound result) {
        String message = String.format("Вопрос/Ответ с ID '%s' не найден", result.getFaqId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
