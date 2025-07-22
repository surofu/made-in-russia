package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.packageOption.ProductPackageOptionView;
import com.surofu.madeinrussia.infrastructure.persistence.product.packageOption.ProductPackageOptionWithTranslationsView;

import java.util.List;

public interface ProductPackageOptionsRepository {
    List<ProductPackageOptionView> getAllViewsByProductIdAndLang(Long productId, String lang);

    List<ProductPackageOptionWithTranslationsView> getAllViewsWithTranslationsByProductIdAndLang(Long productId, String lang);
}
