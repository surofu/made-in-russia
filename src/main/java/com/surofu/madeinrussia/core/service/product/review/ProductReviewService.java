package com.surofu.madeinrussia.core.service.product.review;

import com.surofu.madeinrussia.core.service.product.review.operation.CreateProductReview;
import com.surofu.madeinrussia.core.service.product.review.operation.DeleteProductReview;
import com.surofu.madeinrussia.core.service.product.review.operation.GetProductReviewPageByProductId;
import com.surofu.madeinrussia.core.service.product.review.operation.UpdateProductReview;

public interface ProductReviewService {
    GetProductReviewPageByProductId.Result getProductReviewPageByProductId(GetProductReviewPageByProductId operation);

    CreateProductReview.Result createProductReview(CreateProductReview operation);

    UpdateProductReview.Result updateProductReview(UpdateProductReview operation);

    DeleteProductReview.Result deleteProductReview(DeleteProductReview operation);
}
