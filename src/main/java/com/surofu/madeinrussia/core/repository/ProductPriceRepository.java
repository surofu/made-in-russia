package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.price.ProductPriceView;

import java.util.List;

public interface ProductPriceRepository {
    List<ProductPriceView> findAllViewsByProductId(Long productId);
}
