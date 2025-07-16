package com.surofu.madeinrussia.infrastructure.persistence.product.price;

import com.surofu.madeinrussia.core.repository.ProductPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductPriceRepository implements ProductPriceRepository {

    private final SpringDataProductPriceRepository repository;

    @Override
    public List<ProductPriceView> findAllViewsByProductId(Long productId) {
        return repository.findAllByProduct_Id(productId);
    }
}
