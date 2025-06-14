package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.productReview.productReviewMedia.ProductReviewMedia;

import java.util.List;

public interface ProductReviewMediaRepository {
    List<ProductReviewMedia> findAllByProductId(Long productId, int limit);
}
