package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.product.operation.GetProductMediaByProductId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductMediaByProductIdMapperResultToResponseEntity
        implements GetProductMediaByProductId.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProductMediaByProductId.Result.Success result) {
        return new ResponseEntity<>(result.getProductMediaDtos(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetProductMediaByProductId.Result.NotFound result) {
        String message = String.format("Product with ID '%s' not found", result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
