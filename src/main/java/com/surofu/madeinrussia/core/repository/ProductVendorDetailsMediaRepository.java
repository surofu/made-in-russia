package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.vendorDetails.media.ProductVendorDetailsMediaView;
import com.surofu.madeinrussia.infrastructure.persistence.product.vendorDetails.media.ProductVendorDetailsMediaWithTranslationsView;

import java.util.List;

public interface ProductVendorDetailsMediaRepository {
    List<ProductVendorDetailsMediaView> getAllViewsByProductVendorDetailsIdAndLang(Long id, String lang);

    List<ProductVendorDetailsMediaWithTranslationsView> getAllViewsWithTranslationsByProductVendorDetailsId(Long id, String lang);
}
