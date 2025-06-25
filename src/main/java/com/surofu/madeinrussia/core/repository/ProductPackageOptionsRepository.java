package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.productPackageOption.ProductPackageOption;

import java.util.List;

public interface ProductPackageOptionsRepository {
    List<ProductPackageOption> findAllByProductId(Long productId);
}
