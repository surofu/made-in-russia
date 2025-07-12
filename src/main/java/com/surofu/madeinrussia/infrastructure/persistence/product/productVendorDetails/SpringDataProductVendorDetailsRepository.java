package com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails;

import com.surofu.madeinrussia.core.model.product.productVendorDetails.ProductVendorDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataProductVendorDetailsRepository extends JpaRepository<ProductVendorDetails, Long> {

    Optional<ProductVendorDetailsView> findViewByProductId(@Param("id") Long id);
}
