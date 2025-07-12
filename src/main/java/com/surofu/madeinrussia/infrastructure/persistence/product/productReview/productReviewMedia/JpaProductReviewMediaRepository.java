package com.surofu.madeinrussia.infrastructure.persistence.product.productReview.productReviewMedia;

import com.surofu.madeinrussia.core.repository.ProductReviewMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductReviewMediaRepository implements ProductReviewMediaRepository {

    private final SpringDataProductReviewMediaRepository repository;

    @Override
    public List<ProductReviewMediaView> getAllViewsByProductId(Long productId) {
        return repository.findAllByProductReview_Product_Id(productId);
    }
}
