package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.product.operation.GetProductDeliveryMethodsByProductId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductDeliveryMethodsByProductIdMapperResultToResponseEntity
        implements GetProductDeliveryMethodsByProductId.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProductDeliveryMethodsByProductId.Result.Success result) {
        return new ResponseEntity<>(result.getProductDeliveryMethodDtos(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetProductDeliveryMethodsByProductId.Result.NotFound result) {
        String message = String.format("Product with Id '%s' not found", result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
