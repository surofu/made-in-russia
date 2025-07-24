package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.vendorDetails.ProductVendorDetailsView;
import com.surofu.madeinrussia.infrastructure.persistence.product.vendorDetails.ProductVendorDetailsWithTranslationsView;

import java.util.Optional;

public interface ProductVendorDetailsRepository {
    Optional<ProductVendorDetailsView> getViewByProductIdAndLang(Long productId, String lang);

    Optional<ProductVendorDetailsWithTranslationsView> getViewWithTranslationsByProductIdAndLang(Long productId, String lang);
}
