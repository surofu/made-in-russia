package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.vendorDetails.site.VendorSite;

import java.util.List;

public interface VendorSiteRepository {
    List<VendorSite> getAllByVendorDetailsId(Long id);
}
