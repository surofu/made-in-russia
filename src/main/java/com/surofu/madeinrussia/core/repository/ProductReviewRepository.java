package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository {
    Page<ProductReview> findAll(Specification<ProductReview> spec, Pageable pageable);

    List<ProductReview> findByIdInWithMedia(List<Long> ids);

    Optional<ProductReview> findById(Long id);

    Double findAverageRatingByVendorId(Long vendorId);

    void save(ProductReview productReview);

    boolean isUserOwnerOfProductReview(Long userId, Long productReviewId);
}
