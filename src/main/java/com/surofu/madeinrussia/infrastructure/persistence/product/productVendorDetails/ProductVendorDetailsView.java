package com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails;

import com.surofu.madeinrussia.core.model.product.productVendorDetails.ProductVendorDetailsCreationDate;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.ProductVendorDetailsDescription;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.ProductVendorDetailsLastModificationDate;

public interface ProductVendorDetailsView {
    Long getId();

    ProductVendorDetailsDescription getDescription();

    ProductVendorDetailsCreationDate getCreationDate();

    ProductVendorDetailsLastModificationDate getLastModificationDate();
}
