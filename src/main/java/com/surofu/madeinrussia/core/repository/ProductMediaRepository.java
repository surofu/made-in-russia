package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.media.ProductMediaView;

import java.util.List;

public interface ProductMediaRepository {
    List<ProductMediaView> getAllViewsByProductId(Long productId);
}
