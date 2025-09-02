package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.vendorDetails.phoneNumber.VendorPhoneNumber;

import java.util.Collection;
import java.util.List;

public interface VendorPhoneNumberRepository {
    List<VendorPhoneNumber> getAllByVendorDetailsId(Long vendorDetailsId);

    void saveAll(Collection<VendorPhoneNumber> vendorPhoneNumbers);

    void deleteAll(Collection<VendorPhoneNumber> vendorPhoneNumbers);

    void flush();
}
