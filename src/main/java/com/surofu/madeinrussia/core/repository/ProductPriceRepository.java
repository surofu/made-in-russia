package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.productPrice.ProductPrice;

import java.util.List;

public interface ProductPriceRepository {
    List<ProductPrice> findAllByProductId(Long productId);
}
