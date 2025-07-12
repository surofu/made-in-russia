package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.productCharacteristic.ProductCharacteristicView;

import java.util.List;

public interface ProductCharacteristicRepository {
    List<ProductCharacteristicView> findAllViewsByProductId(Long productId);
}
