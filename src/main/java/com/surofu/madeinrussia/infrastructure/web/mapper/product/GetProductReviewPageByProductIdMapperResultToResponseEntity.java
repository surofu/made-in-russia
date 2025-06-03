package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.error.ProductNotFoundByIdResponseErrorDto;
import com.surofu.madeinrussia.core.service.product.operation.GetProductReviewPageByProductId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductReviewPageByProductIdMapperResultToResponseEntity
        implements GetProductReviewPageByProductId.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProductReviewPageByProductId.Result.Success result) {
        return new ResponseEntity<>(result.getProductReviewDtoPage(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetProductReviewPageByProductId.Result.NotFound result) {
        return new ResponseEntity<>(ProductNotFoundByIdResponseErrorDto.of(result.getProductId()), HttpStatus.NOT_FOUND);
    }
}
