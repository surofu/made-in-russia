package com.surofu.madeinrussia.infrastructure.persistence.vendor.productCategory;

import com.surofu.madeinrussia.core.model.vendorDetails.productCategory.VendorProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataVendorProductCountryRepository extends JpaRepository<VendorProductCategory, Long> {

    @Query(value = """
            select
            id,
            coalesce(
                name_translations -> :lang,
                name
            ) as name,
            creation_date,
            last_modification_date
            from vendor_product_categories
            where vendor_details_id = :id
            """, nativeQuery = true)
    List<VendorProductCategoryView> findAllViewsByVendorDetailsIdAndLang(@Param("id") Long id, @Param("lang") String lang);

    List<VendorProductCategory> findAllByVendorDetailsId(Long id);
}
