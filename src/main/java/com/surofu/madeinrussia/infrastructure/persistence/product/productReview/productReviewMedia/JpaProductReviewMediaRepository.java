package com.surofu.madeinrussia.infrastructure.persistence.product.productReview.productReviewMedia;

import com.surofu.madeinrussia.core.model.product.productReview.productReviewMedia.ProductReviewMedia;
import com.surofu.madeinrussia.core.repository.ProductReviewMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductReviewMediaRepository implements ProductReviewMediaRepository {

    private final SpringDataProductReviewMediaRepository repository;

    @Override
    public List<ProductReviewMedia> findAllByProductId(Long productId, int limit) {
        return repository.findAllByProductId(productId, limit);
    }
}
