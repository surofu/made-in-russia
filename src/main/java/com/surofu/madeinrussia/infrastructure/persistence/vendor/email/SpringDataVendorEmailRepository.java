package com.surofu.madeinrussia.infrastructure.persistence.vendor.email;

import com.surofu.madeinrussia.core.model.vendorDetails.email.VendorEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataVendorEmailRepository extends JpaRepository<VendorEmail, Long> {
    List<VendorEmail> getAllByVendorDetails_Id(Long vendorDetailsId);
}
