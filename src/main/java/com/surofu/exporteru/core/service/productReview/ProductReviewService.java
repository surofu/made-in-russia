package com.surofu.exporteru.core.service.productReview;

import com.surofu.exporteru.core.service.productReview.operation.CreateProductReview;
import com.surofu.exporteru.core.service.productReview.operation.DeleteProductReview;
import com.surofu.exporteru.core.service.productReview.operation.DeleteProductReviewById;
import com.surofu.exporteru.core.service.productReview.operation.GetProductReviewPage;
import com.surofu.exporteru.core.service.productReview.operation.GetProductReviewPageByProductId;
import com.surofu.exporteru.core.service.productReview.operation.UpdateProductReview;

public interface ProductReviewService {
    GetProductReviewPage.Result getProductReviewPage(GetProductReviewPage operation);

    GetProductReviewPageByProductId.Result getProductReviewPageByProductId(GetProductReviewPageByProductId operation);

    CreateProductReview.Result createProductReview(CreateProductReview operation);

    UpdateProductReview.Result updateProductReview(UpdateProductReview operation);

    DeleteProductReview.Result deleteProductReview(DeleteProductReview operation);

    DeleteProductReviewById.Result deleteProductReviewById(DeleteProductReviewById operation);
}
