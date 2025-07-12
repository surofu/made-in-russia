package com.surofu.madeinrussia.infrastructure.persistence.vendor.productCategory;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataVendorProductCountryRepository extends JpaRepository<VendorProductCategory, Long> {

    List<VendorProductCategoryView> findAllViewsByVendorDetails_Id(Long id);
}
