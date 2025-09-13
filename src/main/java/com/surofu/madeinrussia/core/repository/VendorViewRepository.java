package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.vendorDetails.view.VendorView;

public interface VendorViewRepository {
     Long getCountByVendorDetailsId(Long vendorDetailsId);

     void save(VendorView vendorView);

     boolean notExists(VendorView vendorView);
}
