package com.surofu.madeinrussia.core.service.productReview;

import com.surofu.madeinrussia.core.service.productReview.operation.GetProductReviewPageByProductId;

public interface ProductReviewService {
    GetProductReviewPageByProductId.Result getProductReviewPageByProductId(GetProductReviewPageByProductId operation);
}
