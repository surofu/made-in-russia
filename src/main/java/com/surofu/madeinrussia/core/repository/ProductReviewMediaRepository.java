package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.review.media.ProductReviewMediaView;

import java.util.List;

public interface ProductReviewMediaRepository {
    List<ProductReviewMediaView> getAllViewsByProductId(Long productId);
}
