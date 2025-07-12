package com.surofu.madeinrussia.infrastructure.persistence.vendor;

import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataVendorDetailsRepository extends JpaRepository<VendorDetails, Long> {

    Optional<VendorDetailsView> findViewById(@Param("id") Long id);

    @Query("""
    select count(v) from VendorView v
    where v.vendorDetails.id = :id
    """)
    Long findViewsCountById(@Param("id") Long id);
}
