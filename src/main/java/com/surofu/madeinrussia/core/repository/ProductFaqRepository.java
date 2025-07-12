package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.productFaq.ProductFaqView;

import java.util.List;

public interface ProductFaqRepository {
    List<ProductFaqView> findAllViewsByProductId(Long productId);
}
