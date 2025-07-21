package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.characteristic.ProductCharacteristicView;
import com.surofu.madeinrussia.infrastructure.persistence.product.characteristic.ProductCharacteristicWithTranslationsView;

import java.util.List;

public interface ProductCharacteristicRepository {
    List<ProductCharacteristicView> findAllViewsByProductIdAndLang(Long productId, String lang);

    List<ProductCharacteristicWithTranslationsView> findAllViewsWithTranslationsByProductId(Long productId);
}
