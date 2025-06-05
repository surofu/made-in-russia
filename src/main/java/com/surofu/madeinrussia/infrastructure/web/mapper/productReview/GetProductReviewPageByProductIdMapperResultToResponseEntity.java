package com.surofu.madeinrussia.infrastructure.web.mapper.productReview;

import com.surofu.madeinrussia.core.service.productReview.operation.GetProductReviewPageByProductId;
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
}
