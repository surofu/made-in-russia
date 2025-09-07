package com.surofu.madeinrussia.infrastructure.persistence.product.review.media;

import com.surofu.madeinrussia.core.model.product.review.media.ProductReviewMedia;
import com.surofu.madeinrussia.core.repository.ProductReviewMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductReviewMediaRepository implements ProductReviewMediaRepository {

    private final SpringDataProductReviewMediaRepository repository;

    @Override
    public List<ProductReviewMedia> getAllByProductId(Long productId) {
        return repository.findAllByProductReview_Product_Id(productId);
    }

    @Override
    public List<ProductReviewMediaView> getAllViewsByProductId(Long productId) {
        return repository.findAllViewsByProductReview_Product_Id(productId);
    }

    @Override
    public void deleteAll(Collection<ProductReviewMedia> productReviewMedia) {
        repository.deleteAll(productReviewMedia);
    }
}
