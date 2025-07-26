package com.surofu.madeinrussia.infrastructure.web.mapper.faq;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.faq.operation.GetFaqWithTranslationsById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetFaqWithTranslationsByIdMapperResultToResponseEntity
        implements GetFaqWithTranslationsById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetFaqWithTranslationsById.Result.Success result) {
        return new ResponseEntity<>(result.getDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetFaqWithTranslationsById.Result.NotFound result) {
        String message = localizationManager.localize("faq.not_found_by_id", result.getFaqId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
