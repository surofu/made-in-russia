package com.surofu.madeinrussia.core.service.product.review;

import com.surofu.madeinrussia.core.service.product.review.operation.*;

public interface ProductReviewService {
    GetProductReviewPage.Result getProductReviewPage(GetProductReviewPage operation);

    GetProductReviewPageByProductId.Result getProductReviewPageByProductId(GetProductReviewPageByProductId operation);

    CreateProductReview.Result createProductReview(CreateProductReview operation);

    UpdateProductReview.Result updateProductReview(UpdateProductReview operation);

    DeleteProductReview.Result deleteProductReview(DeleteProductReview operation);

    DeleteProductReviewById.Result deleteProductReviewById(DeleteProductReviewById operation);
}
