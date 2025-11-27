package com.surofu.exporteru.infrastructure.web.mapper.product.review;

import com.surofu.exporteru.core.service.productReview.operation.GetProductReviewPageByProductId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductReviewPageByProductIdMapperResultToResponseEntity
        implements GetProductReviewPageByProductId.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProductReviewPageByProductId.Result.Success result) {
        return new ResponseEntity<>(result.getPage(), HttpStatus.OK);
    }
}
