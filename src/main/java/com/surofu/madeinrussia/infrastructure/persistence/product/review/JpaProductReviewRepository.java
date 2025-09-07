package com.surofu.madeinrussia.infrastructure.persistence.product.review;

import com.surofu.madeinrussia.core.model.moderation.ApproveStatus;
import com.surofu.madeinrussia.core.model.product.review.ProductReview;
import com.surofu.madeinrussia.core.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaProductReviewRepository implements ProductReviewRepository {

    private final SpringDataProductReviewRepository repository;

    @Override
    public Page<ProductReview> getPage(Specification<ProductReview> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public List<ProductReview> getByIdInWithMedia(List<Long> ids) {
        return repository.findAllByMediaIdInAndApproveStatus(ids, ApproveStatus.APPROVED);
    }

    @Override
    public Optional<ProductReview> getById(Long id) {
        return repository.findByIdAndApproveStatus(id, ApproveStatus.APPROVED);
    }

    @Override
    public Optional<ProductReview> getByIdWithAnyApproveStatus(Long id) {
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

    @Override
    public List<ProductReview> getAllByProductId(Long id) {
        return repository.findAllByProductId(id);
    }

    @Override
    public void deleteAll(Collection<ProductReview> productReviews) {
        repository.deleteAll(productReviews);
    }
}
