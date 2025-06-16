package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProductReviewRepository {
    Page<ProductReview> findAll(Specification<ProductReview> spec, Pageable pageable);

    List<ProductReview> findByIdInWithMedia(List<Long> ids);

    Double findAverageRatingByVendorId(Long vendorId);

    ProductReview save(ProductReview productReview);
}
