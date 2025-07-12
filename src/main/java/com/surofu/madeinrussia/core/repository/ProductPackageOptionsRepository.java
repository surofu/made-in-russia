package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.productPackageOption.ProductPackageOption;
import com.surofu.madeinrussia.infrastructure.persistence.product.productPackageOption.ProductPackageOptionView;

import java.util.List;

public interface ProductPackageOptionsRepository {
    List<ProductPackageOption> getAllByProductId(Long productId);

    List<ProductPackageOptionView> getAllViewsByProductId(Long productId);
}
