package com.surofu.madeinrussia.infrastructure.persistence.product.productFaq;

import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaq;
import com.surofu.madeinrussia.core.repository.ProductFaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductFaqRepository implements ProductFaqRepository {

    private final SpringDataProductFaqRepository repository;

    @Override
    public List<ProductFaq> findAllByProductId(Long productId) {
        return repository.findAllByProduct_Id(productId);
    }
}
