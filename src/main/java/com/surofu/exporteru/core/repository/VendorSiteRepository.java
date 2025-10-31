package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.vendorDetails.site.VendorSite;

import java.util.Collection;
import java.util.List;

public interface VendorSiteRepository {
    List<VendorSite> getAllByVendorDetailsId(Long id);

    void saveAll(Collection<VendorSite> vendorSites);

    void deleteAll(Collection<VendorSite> vendorSites);

    void flush();
}
