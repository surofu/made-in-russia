package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.product.operation.GetProductDeliveryMethodsByProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetProductDeliveryMethodsByProductIdMapperResultToResponseEntity
        implements GetProductDeliveryMethodsByProductId.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetProductDeliveryMethodsByProductId.Result.Success result) {
        return new ResponseEntity<>(result.getProductDeliveryMethodDtos(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetProductDeliveryMethodsByProductId.Result.NotFound result) {
        String message = localizationManager.localize("product.not_found_by_id", result.getProductId());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.NOT_FOUND);
    }
}
