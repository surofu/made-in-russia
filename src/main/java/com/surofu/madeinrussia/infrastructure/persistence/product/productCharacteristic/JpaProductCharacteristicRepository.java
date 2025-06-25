package com.surofu.madeinrussia.infrastructure.persistence.product.productCharacteristic;

import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.repository.ProductCharacteristicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductCharacteristicRepository implements ProductCharacteristicRepository {

    private final SpringDataProductCharacteristicRepository repository;

    @Override
    public List<ProductCharacteristic> findAllByProductId(Long productId) {
        return repository.findAllByProduct_Id(productId);
    }
}
