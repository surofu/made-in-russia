package com.surofu.exporteru.infrastructure.web.mapper.advertisement;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.advertisement.operation.GetAdvertisementWithTranslationsById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAdvertisementWithTranslationsByIdMapperResultToResponseEntity
        implements GetAdvertisementWithTranslationsById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetAdvertisementWithTranslationsById.Result.Success result) {
        return new ResponseEntity<>(result.getAdvertisementDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetAdvertisementWithTranslationsById.Result.NotFound result) {
        String message = localizationManager.localize("advertisement.not_found_by_id", result.getAdvertisementId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
