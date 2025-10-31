package com.surofu.exporteru.infrastructure.persistence.product.review.media;

import com.surofu.exporteru.core.model.product.review.media.ProductReviewMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProductReviewMediaRepository extends JpaRepository<ProductReviewMedia, Long> {
    @Query("select m from ProductReviewMedia m where m.productReview.product.id = :productId")
    List<ProductReviewMediaView> findAllViewsByProductReview_Product_Id(@Param("productId") Long productId);

    List<ProductReviewMedia> findAllByProductReview_Product_Id(Long productId);
}
