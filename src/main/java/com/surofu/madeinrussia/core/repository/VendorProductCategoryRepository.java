package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.application.annotation.Bench;
import com.surofu.madeinrussia.core.model.vendorDetails.productCategory.VendorProductCategory;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.productCategory.VendorProductCategoryView;

import java.util.Collection;
import java.util.List;

public interface VendorProductCategoryRepository {

    List<VendorProductCategoryView> getAllViewsByVendorDetailsIdAndLang(Long id, String lang);

    void saveAll(Collection<VendorProductCategory> vendorProductCategories);

    void deleteAll(Collection<VendorProductCategory> vendorProductCategories);

    void flush();

    List<VendorProductCategory> getAllByVendorDetailsId(Long id);
}
