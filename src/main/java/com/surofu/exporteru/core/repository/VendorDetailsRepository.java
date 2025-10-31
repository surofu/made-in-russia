package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsInn;

public interface VendorDetailsRepository {

    Long getViewsCountById(Long id);

    boolean existsByInn(VendorDetailsInn inn);

    boolean existsByInnAndNotVendorDetailsId(VendorDetailsInn inn, Long vendorId);

    VendorDetails save(VendorDetails vendorDetails);
}
