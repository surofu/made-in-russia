package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.review.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository {
    Page<ProductReview> getPage(Specification<ProductReview> spec, Pageable pageable);

    List<ProductReview> getByIdInWithMedia(List<Long> ids);

    Optional<ProductReview> getById(Long id);

    Double findAverageRatingByVendorId(Long vendorId);

    Long getCountByProductIdAndUserId(Long productId, Long userId);

    void save(ProductReview productReview);

    void delete(ProductReview productReview);

    boolean isUserOwnerOfProductReview(Long userId, Long productReviewId);
}
