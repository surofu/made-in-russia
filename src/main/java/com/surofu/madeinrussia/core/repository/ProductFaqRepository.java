package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaq;

import java.util.List;

public interface ProductFaqRepository {
    List<ProductFaq> findAllByProductId(Long productId);
}
