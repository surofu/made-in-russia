package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.packageOption.ProductPackageOption;
import com.surofu.madeinrussia.infrastructure.persistence.product.packageOption.ProductPackageOptionView;
import com.surofu.madeinrussia.infrastructure.persistence.product.packageOption.ProductPackageOptionWithTranslationsView;

import java.util.Collection;
import java.util.List;

public interface ProductPackageOptionsRepository {
    List<ProductPackageOptionView> getAllViewsByProductIdAndLang(Long productId, String lang);

    List<ProductPackageOptionWithTranslationsView> getAllViewsWithTranslationsByProductIdAndLang(Long productId, String lang);

    List<ProductPackageOption> getAllByProductId(Long id);

    void deleteAll(Collection<ProductPackageOption> oldProductPackageOptions);

    void saveAll(Collection<ProductPackageOption> productPackageOptionSet);
}
