package com.surofu.madeinrussia.infrastructure.persistence.vendor.phoneNumber;

import com.surofu.madeinrussia.core.model.vendorDetails.phoneNumber.VendorPhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataVendorPhoneNumberRepository extends JpaRepository<VendorPhoneNumber, Long> {
    List<VendorPhoneNumber> findAllByVendorDetails_Id(Long vendorDetailsId);
}
