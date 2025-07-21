package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails.ProductVendorDetailsView;
import com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails.ProductVendorDetailsWithTranslationsView;

import java.util.Optional;

public interface ProductVendorDetailsRepository {
    Optional<ProductVendorDetailsView> getViewByProductIdAndLang(Long productId, String lang);

    Optional<ProductVendorDetailsWithTranslationsView> getViewWithTranslationsByProductId(Long productId);
}
