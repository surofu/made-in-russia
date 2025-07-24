package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.media.ProductMediaView;
import com.surofu.madeinrussia.infrastructure.persistence.product.media.ProductMediaWithTranslationsView;

import java.util.List;

public interface ProductMediaRepository {
    List<ProductMediaView> getAllViewsByProductIdAndLang(Long productId, String lang);

    List<ProductMediaWithTranslationsView> getAllViewsWithTranslationsByProductIdAndLang(Long productId, String lang);
}
