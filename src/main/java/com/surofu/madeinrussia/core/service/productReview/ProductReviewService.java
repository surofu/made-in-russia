package com.surofu.madeinrussia.core.service.productReview;

import com.surofu.madeinrussia.core.service.productReview.operation.CreateProductReview;
import com.surofu.madeinrussia.core.service.productReview.operation.DeleteProductReview;
import com.surofu.madeinrussia.core.service.productReview.operation.GetProductReviewPageByProductId;
import com.surofu.madeinrussia.core.service.productReview.operation.UpdateProductReview;

public interface ProductReviewService {
    GetProductReviewPageByProductId.Result getProductReviewPageByProductId(GetProductReviewPageByProductId operation);

    CreateProductReview.Result createProductReview(CreateProductReview operation);

    UpdateProductReview.Result updateProductReview(UpdateProductReview operation);

    DeleteProductReview.Result deleteProductReview(DeleteProductReview operation);
}
