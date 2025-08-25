package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.vendorDetails.phoneNumber.VendorPhoneNumber;

import java.util.List;

public interface VendorPhoneNumberRepository {
    List<VendorPhoneNumber> getAllByVendorDetailsId(Long vendorDetailsId);
}
