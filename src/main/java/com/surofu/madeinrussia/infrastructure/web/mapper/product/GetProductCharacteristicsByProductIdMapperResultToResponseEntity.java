package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.product.operation.GetProductCharacteristicsByProductId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductCharacteristicsByProductIdMapperResultToResponseEntity
        implements GetProductCharacteristicsByProductId.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProductCharacteristicsByProductId.Result.Success result) {
        return new ResponseEntity<>(result.getCharacteristicDtos(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetProductCharacteristicsByProductId.Result.NotFound result) {
        String message = String.format("Product with ID '%s' not found", result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
