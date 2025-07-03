package com.surofu.madeinrussia.infrastructure.web.mapper.vendor;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.core.service.vendor.operation.CreateVendorFaq;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CreateVendorFaqMapperResulToResponseEntity
implements CreateVendorFaq.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> process(CreateVendorFaq.Result.Success result) {
        String message = "Вопрос/Ответ был успешно создан";
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }
}
