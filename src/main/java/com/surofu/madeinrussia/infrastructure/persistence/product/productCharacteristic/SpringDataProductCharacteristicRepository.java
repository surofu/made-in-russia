package com.surofu.madeinrussia.infrastructure.persistence.product.productCharacteristic;

import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProductCharacteristicRepository extends JpaRepository<ProductCharacteristic, Long> {
    List<ProductCharacteristic> findAllByProduct_Id(Long productId);
}
