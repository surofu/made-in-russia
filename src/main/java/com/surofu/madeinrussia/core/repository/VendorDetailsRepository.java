package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.vendor.VendorDetailsView;

import java.util.Optional;

public interface VendorDetailsRepository {

    Optional<VendorDetailsView> getViewById(Long id);

    Long getViewsCountById(Long id);
}
