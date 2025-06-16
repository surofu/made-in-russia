package com.surofu.madeinrussia.core.service.productReview;

import com.surofu.madeinrussia.core.service.productReview.operation.CreateProductReview;
import com.surofu.madeinrussia.core.service.productReview.operation.GetProductReviewPageByProductId;

public interface ProductReviewService {
    GetProductReviewPageByProductId.Result getProductReviewPageByProductId(GetProductReviewPageByProductId operation);

    CreateProductReview.Result createProductReview(CreateProductReview operation);
}
