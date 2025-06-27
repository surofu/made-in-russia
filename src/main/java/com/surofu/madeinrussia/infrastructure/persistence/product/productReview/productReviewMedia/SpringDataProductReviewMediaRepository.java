package com.surofu.madeinrussia.infrastructure.persistence.product.productReview.productReviewMedia;

import com.surofu.madeinrussia.core.model.product.productReview.productReviewMedia.ProductReviewMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProductReviewMediaRepository extends JpaRepository<ProductReviewMedia, Long> {
    List<ProductReviewMedia> findAllByProductReview_Product_Id(Long productReviewProductId);
}
