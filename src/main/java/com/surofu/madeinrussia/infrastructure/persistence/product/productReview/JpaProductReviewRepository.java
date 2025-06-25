package com.surofu.madeinrussia.infrastructure.persistence.product.productReview;

import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public Optional<ProductReview> findById(Long id) {
        return productReviewRepository.findById(id);
    }

    @Override
    public Double findAverageRatingByVendorId(Long vendorId) {
        return productReviewRepository.findAverageRatingByProductVendorId(vendorId);
    }

    @Override
    public void save(ProductReview productReview) {
        productReviewRepository.save(productReview);
    }

    @Override
    public void deleteById(Long id) {
        productReviewRepository.deleteById(id);
    }

    @Override
    public boolean isUserOwnerOfProductReview(Long userId, Long productReviewId) {
        return productReviewRepository.isUserOwnerOfProductReview(userId, productReviewId);
    }
}
