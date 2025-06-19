package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorView.VendorView;

public interface VendorViewRepository {
     Long getCountByVendorDetailsId(Long vendorDetailsId);

     void saveVendorView(VendorView vendorView);

     boolean notExists(VendorView vendorView);
}
