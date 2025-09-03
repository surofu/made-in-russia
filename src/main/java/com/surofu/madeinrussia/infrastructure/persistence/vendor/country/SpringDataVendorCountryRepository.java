package com.surofu.madeinrussia.infrastructure.persistence.vendor.country;

import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataVendorCountryRepository extends JpaRepository<VendorCountry, Long> {

    @Query(value = """
            select
            id,
            coalesce(
                name_translations -> :lang,
                name
            ) as name,
            coalesce(
                name_translations::hstore -> 'en',
                name
            ) as value,
            creation_date,
            last_modification_date
            from vendor_countries
            where vendor_details_id = :id
            """, nativeQuery = true)
    List<VendorCountryView> findAllViewsByVendorDetailsIdAndLang(@Param("id") Long id, @Param("lang") String lang);

    List<VendorCountry> findByVendorDetailsId(Long vendorId);

    List<VendorCountry> findAllByVendorDetailsId(Long id);
}
