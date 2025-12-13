package com.surofu.exporteru.infrastructure.web.mapper.product.review;

import com.surofu.exporteru.core.service.productReview.operation.GetProductReviewPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductReviewPageMapperResultToResponseEntity
        implements GetProductReviewPage.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProductReviewPage.Result.Success result) {
        return new ResponseEntity<>(result.getPage(), HttpStatus.OK);
    }
}
