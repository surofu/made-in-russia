package com.surofu.madeinrussia.infrastructure.web.mapper.faq;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.faq.operation.CreateFaq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateFaqMapperResultToResponseEntity
implements CreateFaq.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(CreateFaq.Result.Success result) {
        String message = localizationManager.localize("faq.create.success");
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.CREATED);
    }
}
