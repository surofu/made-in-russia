package com.surofu.madeinrussia.infrastructure.persistence.productReview;

import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductReviewRepository implements ProductReviewRepository {

    private final SpringDataProductReviewRepository productReviewRepository;

    @Override
    public Page<ProductReview> findAll(Specification<ProductReview> spec, Pageable pageable) {
        return productReviewRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProductReview> findByIdInWithMedia(List<Long> ids) {
        return productReviewRepository.findByIdInWithMedia(ids);
    }

    @Override
    public Double findAverageRatingByVendorId(Long vendorId) {
        return productReviewRepository.findAverageRatingByProductUserId(vendorId);
    }
}
