package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.application.annotation.Bench;
import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountry;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.country.VendorCountryView;

import java.util.Collection;
import java.util.List;

public interface VendorCountryRepository {

    List<VendorCountryView> getAllViewsByVendorDetailsIdAndLang(Long id, String lang);

    List<VendorCountry> getAllByVendorDetailsId(Long id);

    List<VendorCountry> getByVendorId(Long vendorId);

    void saveAll(Collection<VendorCountry> vendorCountries);

    void deleteAll(Collection<VendorCountry> vendorSites);

    void flush();
}
