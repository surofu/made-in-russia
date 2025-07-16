package com.surofu.madeinrussia.infrastructure.persistence.vendor;

import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsCreationDate;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsLastModificationDate;
import org.springframework.beans.factory.annotation.Value;

public interface VendorDetailsView {
    Long getId();

    VendorDetailsInn getInn();

    @Value("#{@jpaVendorDetailsRepository.getViewsCountById(target.id)}")
    Long getViewsCount();

    VendorDetailsCreationDate getCreationDate();

    VendorDetailsLastModificationDate getLastModificationDate();
}
