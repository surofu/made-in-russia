package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.vendor.productCategory.VendorProductCategoryView;

import java.util.List;

public interface VendorProductCategoryRepository {

    List<VendorProductCategoryView> getAllViewsByVendorDetailsId(Long id);
}
