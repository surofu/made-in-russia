package com.surofu.madeinrussia.infrastructure.persistence.product.vendorDetails.media;

import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProductVendorDetailsMediaRepository extends JpaRepository<ProductVendorDetailsMedia, Long> {

    @Query(value = """
            select
            m.id,
            m.url,
            coalesce(
                m.alt_text_translations -> :lang,
                m.alt_text
            ) as alt_text,
            m.position,
            m.creation_date,
            m.last_modification_date
            from product_vendor_details_media m
            where m.product_vendor_details_id = :id
            order by m.position
            """, nativeQuery = true)
    List<ProductVendorDetailsMediaView> findAllViewsByProductVendorDetailsIdAndLang(@Param("id") Long id, @Param("lang") String lang);

    @Query(value = """
            select
            m.id,
            m.media_type::media_type,
            m.url,
            coalesce(
                m.alt_text_translations -> :lang,
                m.alt_text
            ) as alt_text,
            m.alt_text_translations::text,
            m.position,
            m.creation_date,
            m.last_modification_date
            from product_vendor_details_media m
            where m.product_vendor_details_id = :id
            order by m.position
            """, nativeQuery = true)
    List<ProductVendorDetailsMediaWithTranslationsView> findAllViewsWithTranslationsByProductVendorDetailsIdAndLang(@Param("id") Long id, @Param("lang") String lang);
}
