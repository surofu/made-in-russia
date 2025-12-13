package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.product.review.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository {
    Page<ProductReview> getPage(Specification<ProductReview> spec, Pageable pageable);

    List<ProductReview> getByIdInWithMedia(List<Long> ids);

    Optional<ProductReview> getById(Long id);

    Optional<ProductReview> getByIdAndUserId(Long id, Long userId);

    Optional<ProductReview> getByIdWithAnyApproveStatus(Long id);

    Double findAverageRatingByVendorId(Long vendorId);

    Long getCountByProductIdAndUserId(Long productId, Long userId);

    void save(ProductReview productReview);

    void delete(ProductReview productReview);

    boolean isUserOwnerOfProductReview(Long userId, Long productReviewId);

    List<ProductReview> getAllByProductId(Long id);

    void deleteAll(Collection<ProductReview> productReviews);
}
