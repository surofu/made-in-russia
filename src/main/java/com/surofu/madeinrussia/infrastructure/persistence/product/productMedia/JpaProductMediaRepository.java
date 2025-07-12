package com.surofu.madeinrussia.infrastructure.persistence.product.productMedia;

import com.surofu.madeinrussia.core.repository.ProductMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductMediaRepository implements ProductMediaRepository {

    private final SpringDataProductMediaRepository repository;

    @Override
    public List<ProductMediaView> getAllViewsByProductId(Long productId) {
        return repository.findAllByProduct_IdOrderByPositionAsc(productId);
    }
}
