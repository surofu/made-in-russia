package com.surofu.madeinrussia.core.service.product;

import com.surofu.madeinrussia.core.service.product.operation.*;

public interface ProductService {
    GetProductById.Result getProductById(GetProductById operation);

    GetProductCategoryByProductId.Result getProductCategoryByProductId(GetProductCategoryByProductId operation);

    GetProductDeliveryMethodsByProductId.Result getProductDeliveryMethodsByProductId(GetProductDeliveryMethodsByProductId operation);

    GetProductMediaByProductId.Result getProductMediaByProductId(GetProductMediaByProductId operation);

    GetProductCharacteristicsByProductId.Result getProductCharacteristicsByProductId(GetProductCharacteristicsByProductId operation);

    GetProductFaqByProductId.Result getProductFaqByProductId(GetProductFaqByProductId operation);

    CreateProduct.Result createProduct(CreateProduct operation);
}
