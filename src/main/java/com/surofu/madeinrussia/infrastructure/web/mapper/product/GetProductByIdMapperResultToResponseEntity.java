package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.product.operation.GetProductById;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductByIdMapperResultToResponseEntity implements GetProductById.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProductById.Result.Success result) {
        return new ResponseEntity<>(result.getProductDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetProductById.Result.NotFound result) {
        SimpleResponseErrorDto errorDto = new SimpleResponseErrorDto("Product with id '%d' not found".formatted(result.getProductId()));
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
