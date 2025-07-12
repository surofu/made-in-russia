package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.productMedia.ProductMediaView;

import java.util.List;

public interface ProductMediaRepository {
    List<ProductMediaView> getAllViewsByProductId(Long productId);
}
