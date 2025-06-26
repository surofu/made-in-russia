package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;

import java.util.List;

public interface ProductMediaRepository {
    List<ProductMedia> findAllByProductId(Long productId);
}
