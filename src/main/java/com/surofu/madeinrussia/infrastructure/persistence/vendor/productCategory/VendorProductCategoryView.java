package com.surofu.madeinrussia.infrastructure.persistence.vendor.productCategory;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategoryCreationDate;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategoryLastModificationDate;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategoryName;

public interface VendorProductCategoryView {
    Long getId();

    VendorProductCategoryName getName();

    VendorProductCategoryCreationDate getCreationDate();

    VendorProductCategoryLastModificationDate getLastModificationDate();
}
