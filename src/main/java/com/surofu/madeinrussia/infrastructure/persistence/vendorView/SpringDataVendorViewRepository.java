package com.surofu.madeinrussia.infrastructure.persistence.vendorView;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorView.VendorView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataVendorViewRepository extends JpaRepository<VendorView, Long> {

    @Query("select count(v) from VendorView v where v.vendorDetails.id = :vendorDetailsId")
    Long countByVendorDetailsId(@Param("vendorDetailsId") Long vendorDetailsId);

    @Query("""
            select count(v) <= 0 from VendorView v
            where v.vendorDetails.id = :vendorDetailsId and
            v.user.id = :userId
            """)
    boolean notExistsByVendorDetailsIdAndUserId(@Param("vendorDetailsId") Long vendorDetailsId, @Param("userId") Long userId);
}
