package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.vendorDetails.view.VendorView;

public interface VendorViewRepository {
     Long getCountByVendorDetailsId(Long vendorDetailsId);

     void save(VendorView vendorView);

     boolean notExists(VendorView vendorView);

     Boolean existsByUserIdAndVendorDetailsId(Long userId, Long vendorDetailsId);
}
