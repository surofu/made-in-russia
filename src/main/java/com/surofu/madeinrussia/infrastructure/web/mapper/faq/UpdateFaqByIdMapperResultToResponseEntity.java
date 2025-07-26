package com.surofu.madeinrussia.infrastructure.web.mapper.faq;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.faq.operation.UpdateFaqById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateFaqByIdMapperResultToResponseEntity
        implements UpdateFaqById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(UpdateFaqById.Result.Success result) {
        String message = localizationManager.localize("faq.update.success", result.getFaqId());
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(UpdateFaqById.Result.NotFound result) {
        String message = localizationManager.localize("faq.not_found_by_id", result.getFaqId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processEmptyTranslations(UpdateFaqById.Result.EmptyTranslations result) {
        String message = localizationManager.localize("translation.translation_error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processTranslationError(UpdateFaqById.Result.TranslationError error) {
        String message = localizationManager.localize("translation.translation_error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processSaveFaqError(UpdateFaqById.Result.SaveFaqError error) {
        String message = localizationManager.localize("faq.save_error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
