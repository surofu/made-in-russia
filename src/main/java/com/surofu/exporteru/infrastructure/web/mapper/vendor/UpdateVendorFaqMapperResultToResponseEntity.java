package com.surofu.exporteru.infrastructure.web.mapper.vendor;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.vendor.operation.UpdateVendorFaq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateVendorFaqMapperResultToResponseEntity
        implements UpdateVendorFaq.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(UpdateVendorFaq.Result.Success result) {
        return new ResponseEntity<>(result.getFaq(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(UpdateVendorFaq.Result.NotFound result) {
        String message = localizationManager.localize("faq.not_found_by_id", result.getId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processTranslationError(UpdateVendorFaq.Result.TranslationError result) {
        String message = localizationManager.localize("translation.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processSaveError(UpdateVendorFaq.Result.SaveError result) {
        String message = localizationManager.localize("faq.save.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
