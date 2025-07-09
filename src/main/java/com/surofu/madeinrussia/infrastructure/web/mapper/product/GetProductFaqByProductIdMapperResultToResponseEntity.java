package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.product.operation.GetProductFaqByProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetProductFaqByProductIdMapperResultToResponseEntity
        implements GetProductFaqByProductId.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetProductFaqByProductId.Result.Success result) {
        return new ResponseEntity<>(result.getProductFaqDtos(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetProductFaqByProductId.Result.NotFound result) {
        String message = localizationManager.localize("product.not_found_by_id", result.getProductId());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.NOT_FOUND);
    }
}
