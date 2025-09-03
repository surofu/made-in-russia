package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.madeinrussia.infrastructure.persistence.product.characteristic.ProductCharacteristicView;
import com.surofu.madeinrussia.infrastructure.persistence.product.characteristic.ProductCharacteristicWithTranslationsView;

import java.util.Collection;
import java.util.List;

public interface ProductCharacteristicRepository {
    List<ProductCharacteristicView> findAllViewsByProductIdAndLang(Long productId, String lang);

    List<ProductCharacteristicWithTranslationsView> findAllViewsWithTranslationsByProductIdAndLang(Long productId, String lang);

    List<ProductCharacteristic> getAllByProductId(Long id);

    void deleteAll(Collection<ProductCharacteristic> oldProductCharacteristics);

    void saveAll(Collection<ProductCharacteristic> productCharacteristicSet);
}
