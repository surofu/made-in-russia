package com.surofu.madeinrussia.infrastructure.persistence.product.productReview.productReviewMedia;

import com.surofu.madeinrussia.core.model.product.productReview.productReviewMedia.ProductReviewMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProductReviewMediaRepository extends JpaRepository<ProductReviewMedia, Long> {


    @Query("""
                select prm from ProductReviewMedia prm
                where prm.productReview.product.id = :productId
                order by prm.lastModificationDate.value DESC
                limit :limit
            """)
    List<ProductReviewMedia> findAllByProductId(@Param("productId") Long productId, @Param("limit") int limit);
}
