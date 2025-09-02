package com.surofu.madeinrussia.infrastructure.persistence.product.media;

import com.surofu.madeinrussia.core.model.product.media.ProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProductMediaRepository extends JpaRepository<ProductMedia, Long> {

    @Query(value = """
    select
    m.id,
    m.media_type::media_type,
    m.mime_type,
    m.position,
    m.url,
    coalesce(
        m.alt_text_translations -> :lang,
        m.alt_text
    ) as alt_text,
    m.creation_date,
    m.last_modification_date
    from product_media m
    where m.product_id = :productId
    order by m.position
    """, nativeQuery = true)
    List<ProductMediaView> findAllByProductIdAndLang(@Param("productId") Long productId, @Param("lang") String lang);

    @Query(value = """
    select
    m.id,
    m.media_type::media_type,
    m.mime_type,
    m.position,
    m.url,
    coalesce(
        m.alt_text_translations -> :lang,
        m.alt_text
    ) as alt_text,
    m.alt_text_translations::text,
    m.creation_date,
    m.last_modification_date
    from product_media m
    where m.product_id = :productId
    order by m.position
    """, nativeQuery = true)
    List<ProductMediaWithTranslationsView> findAllWithTranslationsByProductIdAndLang(@Param("productId") Long productId, @Param("lang") String lang);

    List<ProductMedia> findAllByProductId(Long id);
}
