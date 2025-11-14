package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsInn;

import java.util.Optional;

public interface VendorDetailsRepository {

    Optional<VendorDetails> getById(Long id);

    Long getViewsCountById(Long id);

    boolean existsByInn(VendorDetailsInn inn);

    boolean existsByInnAndNotVendorDetailsId(VendorDetailsInn inn, Long vendorId);

    VendorDetails save(VendorDetails vendorDetails);
}
