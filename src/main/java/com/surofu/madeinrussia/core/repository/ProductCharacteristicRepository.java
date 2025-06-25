package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;

import java.util.List;

public interface ProductCharacteristicRepository {
    List<ProductCharacteristic> findAllByProductId(Long productId);
}
