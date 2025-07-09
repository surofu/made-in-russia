package com.surofu.madeinrussia.infrastructure.web.mapper.vendor;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.vendor.operation.CreateVendorFaq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateVendorFaqMapperResulToResponseEntity
implements CreateVendorFaq.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> process(CreateVendorFaq.Result.Success result) {
        String message = localizationManager.localize("faq.create.success");
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }
}
