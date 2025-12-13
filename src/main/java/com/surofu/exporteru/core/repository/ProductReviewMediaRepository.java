package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.product.review.media.ProductReviewMedia;
import com.surofu.exporteru.infrastructure.persistence.product.review.media.ProductReviewMediaView;

import java.util.Collection;
import java.util.List;

public interface ProductReviewMediaRepository {
    List<ProductReviewMedia> getAllByProductId(Long productId);

    List<ProductReviewMediaView> getAllViewsByProductId(Long productId);

    void deleteAll(Collection<ProductReviewMedia> productReviewMedia);
}
