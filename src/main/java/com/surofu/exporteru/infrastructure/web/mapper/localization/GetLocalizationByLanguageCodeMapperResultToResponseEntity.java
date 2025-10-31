package com.surofu.exporteru.infrastructure.web.mapper.localization;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.localization.service.GetLocalizationByLanguageCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetLocalizationByLanguageCodeMapperResultToResponseEntity
implements GetLocalizationByLanguageCode.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetLocalizationByLanguageCode.Result.Success result) {
        return new ResponseEntity<>(result.getContent(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetLocalizationByLanguageCode.Result.NotFound result) {
        String message = localizationManager.localize("localization.error.not_found.by_language_code", result.getLanguageCode());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
