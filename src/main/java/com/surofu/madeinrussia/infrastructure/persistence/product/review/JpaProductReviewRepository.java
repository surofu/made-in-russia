package com.surofu.madeinrussia.infrastructure.persistence.product.review;

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

    private final SpringDataProductReviewRepository repository;

    @Override
    public Page<ProductReview> findAll(Specification<ProductReview> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public List<ProductReview> findByIdInWithMedia(List<Long> ids) {
        return repository.findByIdInWithMedia(ids);
    }

    @Override
    public Optional<ProductReview> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Double findAverageRatingByVendorId(Long vendorId) {
        return repository.findAverageRatingByProductVendorId(vendorId);
    }

    @Override
    public Long getCountByProductIdAndUserId(Long productId, Long userId) {
        return repository.countByProduct_IdAndUser_Id(productId, userId);
    }

    @Override
    public void save(ProductReview productReview) {
        repository.save(productReview);
    }

    @Override
    public void delete(ProductReview productReview) {
        repository.delete(productReview);
    }

    @Override
    public boolean isUserOwnerOfProductReview(Long userId, Long productReviewId) {
        return !repository.isUserOwnerOfProductReview(userId, productReviewId);
    }
}
