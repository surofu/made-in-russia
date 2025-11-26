package com.surofu.exporteru.infrastructure.persistence.vendor;

import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataVendorDetailsRepository extends JpaRepository<VendorDetails, Long> {

  @Query("""
      select count(v) from VendorView v
      where v.vendorDetails.id = :id
      """)
  Long findViewsCountById(@Param("id") Long id);

  boolean existsByInn_Value(String innValue);

  boolean existsByInn_ValueAndIdNot(String inn, Long id);

  @Query("select p.user.vendorDetails from Product p where p.id = :productId")
  List<VendorDetails> findByProductId(@Param("productId") Long productId);
}
