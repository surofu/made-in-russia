package com.surofu.madeinrussia.infrastructure.persistence.vendor;

import com.surofu.madeinrussia.core.model.vendorDetails.*;
import org.springframework.beans.factory.annotation.Value;

public interface VendorDetailsView {
    Long getId();

    VendorDetailsInn getInn();

    VendorDetailsDescription getDescription();

    @Value("#{@jpaVendorDetailsRepository.getViewsCountById(target.id)}")
    Long getViewsCount();

    VendorDetailsCreationDate getCreationDate();

    VendorDetailsLastModificationDate getLastModificationDate();
}
