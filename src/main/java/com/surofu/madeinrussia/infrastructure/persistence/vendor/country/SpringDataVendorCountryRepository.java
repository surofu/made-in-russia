package com.surofu.madeinrussia.infrastructure.persistence.vendor.country;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataVendorCountryRepository extends JpaRepository<VendorCountry, Long> {
    List<VendorCountryView> findAllViewsByVendorDetails_Id(Long id);
}
