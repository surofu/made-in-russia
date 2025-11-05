package com.surofu.exporteru.infrastructure.web.mapper.me;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.me.operation.ToggleMeFavoriteProductById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ToggleMeFavoriteProductBuIdMapperResultToResponseEntity
        implements ToggleMeFavoriteProductById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(ToggleMeFavoriteProductById.Result.Success result) {
        return new ResponseEntity<>(result.getStatus(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processProductNotFound(ToggleMeFavoriteProductById.Result.ProductNotFound result) {
        String message = localizationManager.localize("product.not_found_by_id", result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processSaveError(ToggleMeFavoriteProductById.Result.SaveError result) {
        String message = localizationManager.localize("user.save.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
