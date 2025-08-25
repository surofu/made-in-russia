package com.surofu.madeinrussia.infrastructure.persistence.vendorView;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import com.surofu.madeinrussia.core.model.vendorDetails.view.VendorView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataVendorViewRepository extends JpaRepository<VendorView, Long> {

    @Query("select count(v) from VendorView v where v.vendorDetails.id = :vendorDetailsId")
    Long countByVendorDetailsId(@Param("vendorDetailsId") Long vendorDetailsId);

    boolean existsByVendorDetailsAndUser(VendorDetails vendorDetails, User user);
}
