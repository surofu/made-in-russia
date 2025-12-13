package com.surofu.exporteru.infrastructure.persistence.vendor.phoneNumber;

import com.surofu.exporteru.core.model.vendorDetails.phoneNumber.VendorPhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataVendorPhoneNumberRepository extends JpaRepository<VendorPhoneNumber, Long> {
    List<VendorPhoneNumber> findAllByVendorDetails_Id(Long vendorDetailsId);
}
