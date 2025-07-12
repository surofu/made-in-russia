package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.productReview.productReviewMedia.ProductReviewMediaView;

import java.util.List;

public interface ProductReviewMediaRepository {
    List<ProductReviewMediaView> getAllViewsByProductId(Long productId);
}
