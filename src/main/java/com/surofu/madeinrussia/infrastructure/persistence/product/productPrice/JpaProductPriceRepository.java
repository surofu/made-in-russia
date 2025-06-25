package com.surofu.madeinrussia.infrastructure.persistence.product.productPrice;

import com.surofu.madeinrussia.core.model.product.productPrice.ProductPrice;
import com.surofu.madeinrussia.core.repository.ProductPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductPriceRepository implements ProductPriceRepository {

    private final SpringDataProductPriceRepository repository;

    @Override
    public List<ProductPrice> findAllByProductId(Long productId) {
        return repository.findAllByProduct_Id(productId);
    }
}
