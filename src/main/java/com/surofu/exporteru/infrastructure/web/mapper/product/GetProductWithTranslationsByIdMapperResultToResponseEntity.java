package com.surofu.exporteru.infrastructure.web.mapper.product;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.product.operation.GetProductWithTranslationsById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetProductWithTranslationsByIdMapperResultToResponseEntity
implements GetProductWithTranslationsById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetProductWithTranslationsById.Result.Success result) {
        return new ResponseEntity<>(result.getProductWithTranslationsDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetProductWithTranslationsById.Result.NotFound result) {
        String message = localizationManager.localize("product.not_found_by_id", result.getId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
