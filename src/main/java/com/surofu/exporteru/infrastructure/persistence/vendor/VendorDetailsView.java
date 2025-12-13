package com.surofu.exporteru.infrastructure.persistence.vendor;

import com.surofu.exporteru.core.model.vendorDetails.*;
import org.springframework.beans.factory.annotation.Value;

public interface VendorDetailsView {
    Long getId();

    VendorDetailsInn getInn();

    VendorDetailsAddress getAddress();

    VendorDetailsDescription getDescription();

    @Value("#{@jpaVendorDetailsRepository.getViewsCountById(target.id)}")
    Long getViewsCount();

    VendorDetailsCreationDate getCreationDate();

    VendorDetailsLastModificationDate getLastModificationDate();
}
