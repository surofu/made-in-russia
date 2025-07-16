package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.faq.ProductFaqView;

import java.util.List;

public interface ProductFaqRepository {
    List<ProductFaqView> findAllViewsByProductIdAndLang(Long productId, String lang);
}
