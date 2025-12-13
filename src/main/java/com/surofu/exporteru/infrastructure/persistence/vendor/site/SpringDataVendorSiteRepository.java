package com.surofu.exporteru.infrastructure.persistence.vendor.site;

import com.surofu.exporteru.core.model.vendorDetails.site.VendorSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataVendorSiteRepository extends JpaRepository<VendorSite, Long> {
    List<VendorSite> findAllByVendorDetails_Id(Long vendorDetailsId);
}
