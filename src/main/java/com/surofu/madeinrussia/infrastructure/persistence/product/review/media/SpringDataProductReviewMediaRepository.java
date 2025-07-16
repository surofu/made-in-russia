package com.surofu.madeinrussia.infrastructure.persistence.product.review.media;

import com.surofu.madeinrussia.core.model.product.productReview.productReviewMedia.ProductReviewMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProductReviewMediaRepository extends JpaRepository<ProductReviewMedia, Long> {
    List<ProductReviewMediaView> findAllByProductReview_Product_Id(Long productId);
}
