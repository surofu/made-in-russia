package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;

public interface VendorDetailsRepository {

    Long getViewsCountById(Long id);

    boolean existsByInn(VendorDetailsInn inn);
}
