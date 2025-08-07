package com.surofu.madeinrussia.infrastructure.web.mapper.product.review;

import com.surofu.madeinrussia.core.service.product.review.operation.GetProductReviewPage;
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
