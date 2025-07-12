package com.surofu.madeinrussia.infrastructure.persistence.vendor.country;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountryCreationDate;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountryLastModificationDate;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountryName;

public interface VendorCountryView {
    Long getId();

    VendorCountryName getName();

    VendorCountryCreationDate getCreationDate();

    VendorCountryLastModificationDate getLastModificationDate();
}
