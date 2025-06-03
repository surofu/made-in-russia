package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.error.ProductNotFoundByIdResponseErrorDto;
import com.surofu.madeinrussia.core.service.product.operation.GetProductFaqByProductId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductFaqByProductIdMapperResultToResponseEntity implements GetProductFaqByProductId.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProductFaqByProductId.Result.Success result) {
        return new ResponseEntity<>(result.getProductFaqDtos(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetProductFaqByProductId.Result.NotFound result) {
        return new ResponseEntity<>(ProductNotFoundByIdResponseErrorDto.of(result.getProductId()), HttpStatus.NOT_FOUND);
    }
}
