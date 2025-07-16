package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.packageOption.ProductPackageOptionView;

import java.util.List;

public interface ProductPackageOptionsRepository {
    List<ProductPackageOptionView> getAllViewsByProductIdAndLang(Long productId, String lang);
}
