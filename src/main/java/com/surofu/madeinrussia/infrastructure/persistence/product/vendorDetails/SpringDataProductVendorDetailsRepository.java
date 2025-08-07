package com.surofu.madeinrussia.infrastructure.persistence.product.vendorDetails;

import com.surofu.madeinrussia.core.model.product.vendorDetails.ProductVendorDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataProductVendorDetailsRepository extends JpaRepository<ProductVendorDetails, Long> {

    @Query(value = """
            select
            d.id,
            coalesce(
                d.main_description_translations -> :lang,
                d.main_description
            ) as main_description,
            coalesce(
                d.further_description_translations -> :lang,
                d.further_description
            ) as further_description,
            d.creation_date,
            d.last_modification_date
            from product_vendor_details d
            where d.product_id = :productId
            """, nativeQuery = true)
    Optional<ProductVendorDetailsView> findViewByProductIdAndLang(@Param("productId") Long id, @Param("lang") String lang);

    @Query(value = """
            select
            d.id,
            coalesce(
                d.main_description_translations -> :lang,
                d.main_description
            ) as main_description,
            d.main_description_translations::text,
            coalesce(
                d.further_description_translations -> :lang,
                d.further_description
            ) as further_description,
            d.further_description_translations::text,
            d.creation_date,
            d.last_modification_date
            from product_vendor_details d
            where d.product_id = :productId
            """, nativeQuery = true)
    Optional<ProductVendorDetailsWithTranslationsView> findViewWithTranslationsByProductIdAndLang(@Param("productId") Long id, @Param("lang") String lang);
}
