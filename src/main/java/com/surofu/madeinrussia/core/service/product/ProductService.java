package com.surofu.madeinrussia.core.service.product;

import com.surofu.madeinrussia.core.service.product.operation.GetProductById;
import com.surofu.madeinrussia.core.service.product.operation.GetProducts;

public interface ProductService {
    GetProducts.Result getProducts(GetProducts operation);
    GetProductById.Result getProductById(GetProductById operation);
}
