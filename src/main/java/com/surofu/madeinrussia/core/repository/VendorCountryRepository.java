package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.vendor.country.VendorCountryView;

import java.util.List;

public interface VendorCountryRepository {
    List<VendorCountryView> getAllViewsByVendorDetailsId(Long id);
}
