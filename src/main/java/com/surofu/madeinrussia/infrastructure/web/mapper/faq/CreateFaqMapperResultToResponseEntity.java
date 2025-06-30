package com.surofu.madeinrussia.infrastructure.web.mapper.faq;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.core.service.faq.operation.CreateFaq;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CreateFaqMapperResultToResponseEntity
implements CreateFaq.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(CreateFaq.Result.Success result) {
        String message = "Вопрос/Ответ был успешно создан";
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.CREATED);
    }
}
