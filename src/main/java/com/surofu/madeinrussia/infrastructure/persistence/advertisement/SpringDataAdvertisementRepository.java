package com.surofu.madeinrussia.infrastructure.persistence.advertisement;

import com.surofu.madeinrussia.core.model.advertisement.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataAdvertisementRepository extends JpaRepository<Advertisement, Long> {

    @Query(value = """
            select
            a.id,
            coalesce(
                a.title_translations -> :lang,
                a.title
            ) as title,
            coalesce(
                a.subtitle_translations -> :lang,
                a.subtitle
            ) as subtitle,
            a.image_url,
            a.creation_date,
            a.last_modification_date
            from advertisements a
            order by a.creation_date desc
            """, nativeQuery = true)
    List<AdvertisementView> findAllViewsByLang(@Param("lang") String lang);

    @Query(value = """
            select
            a.id,
            coalesce(
                a.title_translations -> :lang,
                a.title
            ) as title,
            coalesce(
                a.subtitle_translations -> :lang,
                a.subtitle
            ) as subtitle,
            a.image_url,
            a.creation_date,
            a.last_modification_date
            from advertisements a
            where a.id = :id
            """, nativeQuery = true)
    Optional<AdvertisementView> findViewByIdAndLang(@Param("id") Long id, @Param("lang") String lang);

    @Query(value = """
            select
            a.id,
            coalesce(
                a.title_translations -> :lang,
                a.title
            ) as title,
            a.title_translations::text,
            coalesce(
                a.subtitle_translations -> :lang,
                a.subtitle
            ) as subtitle,
            a.subtitle_translations::text,
            a.image_url,
            a.creation_date,
            a.last_modification_date
            from advertisements a
            where a.id = :id
            """, nativeQuery = true)
    Optional<AdvertisementWithTranslationsView> findViewWithTranslationsByIdAndLang(@Param("id") Long id, @Param("lang") String lang);
}
