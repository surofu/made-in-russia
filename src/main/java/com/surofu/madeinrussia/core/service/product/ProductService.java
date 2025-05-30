package com.surofu.madeinrussia.core.service.product;

import com.surofu.madeinrussia.core.service.product.operation.*;

public interface ProductService {
    GetProductPage.Result getProductPage(GetProductPage operation);

    GetProductById.Result getProductById(GetProductById operation);

    GetProductCategoryByProductId.Result getProductCategoryByProductId(GetProductCategoryByProductId operation);

    GetProductDeliveryMethodsByProductId.Result getProductDeliveryMethodsByProductId(GetProductDeliveryMethodsByProductId operation);

    GetProductMediaByProductId.Result getProductMediaByProductId(GetProductMediaByProductId operation);

    GetProductCharacteristicsByProductId.Result getProductCharacteristicsByProductId(GetProductCharacteristicsByProductId operation);

    GetProductReviewPageByProductId.Result getProductReviewPageByProductId(GetProductReviewPageByProductId operation);

    GetProductFaqByProductId.Result getProductFaqByProductId(GetProductFaqByProductId operation);
}
