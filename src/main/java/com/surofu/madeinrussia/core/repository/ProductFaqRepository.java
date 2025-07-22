package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.faq.ProductFaqView;
import com.surofu.madeinrussia.infrastructure.persistence.product.faq.ProductFaqWithTranslationsView;

import java.util.List;

public interface ProductFaqRepository {
    List<ProductFaqView> findAllViewsByProductIdAndLang(Long productId, String lang);

    List<ProductFaqWithTranslationsView> findAllWithTranslationsByProductIdAndLang(Long productId, String lang);
}
