package com.surofu.exporteru.infrastructure.persistence.advertisement;

import com.surofu.exporteru.core.model.advertisement.Advertisement;
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
            coalesce(
                a.third_text_translations -> :lang,
                a.third_text
            ) as third_text,
            a.image_url,
            a.link,
            a.is_big,
            a.expiration_date,
            a.creation_date,
            a.last_modification_date
            from advertisements a
            where a.expiration_date > now()
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
            coalesce(
                a.third_text_translations -> :lang,
                a.third_text
            ) as third_text,
            a.image_url,
            a.link,
            a.is_big,
            a.expiration_date,
            a.creation_date,
            a.last_modification_date
            from advertisements a
            where
                a.expiration_date > now() and
                    (
                    :text is null or
                    :text = '' or
                    coalesce(a.title_translations::hstore -> 'en', a.title) ilike concat('%', :text, '%') or
                    coalesce(a.title_translations::hstore -> 'ru', a.title) ilike concat('%', :text, '%') or
                    coalesce(a.title_translations::hstore -> 'zh', a.title) ilike concat('%', :text, '%') or
                    coalesce(a.subtitle_translations::hstore -> 'en', a.subtitle) ilike concat('%', :text, '%') or
                    coalesce(a.subtitle_translations::hstore -> 'ru', a.subtitle) ilike concat('%', :text, '%') or
                    coalesce(a.subtitle_translations::hstore -> 'zh', a.subtitle) ilike concat('%', :text, '%') or
                    coalesce(a.third_text_translations::hstore -> 'en', a.third_text) ilike concat('%', :text, '%') or
                    coalesce(a.third_text_translations::hstore -> 'ru', a.third_text) ilike concat('%', :text, '%') or
                    coalesce(a.third_text_translations::hstore -> 'zh', a.third_text) ilike concat('%', :text, '%')
                    )
            order by
                CASE WHEN :sort = 'id' THEN a.id END,
                CASE WHEN :sort = 'title' THEN coalesce(a.title_translations::hstore -> :lang, a.title) END,
                CASE WHEN :sort = 'subtitle' THEN coalesce(a.subtitle_translations::hstore -> :lang, a.subtitle) END,
                CASE WHEN :sort = 'third_text' THEN coalesce(a.third_text_translations::hstore -> :lang, a.third_text) END,
                CASE WHEN :sort = 'creation_date' THEN a.creation_date END,
                CASE WHEN :sort = 'expiration_date' THEN a.expiration_date END
            """, nativeQuery = true)
    List<AdvertisementView> findAllViewsByLang(@Param("lang") String lang,
                                               @Param("text") String text,
                                               @Param("sort") String sort);
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
            coalesce(
                a.third_text_translations -> :lang,
                a.third_text
            ) as third_text,
            a.third_text_translations::text,
            a.image_url,
            a.link,
            a.is_big,
            a.expiration_date,
            a.creation_date,
            a.last_modification_date
            from advertisements a
            order by a.creation_date desc
            """, nativeQuery = true)
    List<AdvertisementWithTranslationsView> findAllWithTranslationsViewsByLang(@Param("lang") String lang);

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
                    coalesce(
                        a.third_text_translations -> :lang,
                        a.third_text
                    ) as third_text,
                    a.third_text_translations::text,
                    a.image_url,
                    a.link,
                    a.is_big,
                    a.expiration_date,
                    a.creation_date,
                    a.last_modification_date
                    from advertisements a
                    where
                        coalesce(a.title_translations::hstore -> 'en', a.title) ilike concat('%', :text, '%') or
                        coalesce(a.title_translations::hstore -> 'ru', a.title) ilike concat('%', :text, '%') or
                        coalesce(a.title_translations::hstore -> 'zh', a.title) ilike concat('%', :text, '%') or
                        coalesce(a.subtitle_translations::hstore -> 'en', a.subtitle) ilike concat('%', :text, '%') or
                        coalesce(a.subtitle_translations::hstore -> 'ru', a.subtitle) ilike concat('%', :text, '%') or
                        coalesce(a.subtitle_translations::hstore -> 'zh', a.subtitle) ilike concat('%', :text, '%') or
                        coalesce(a.third_text_translations::hstore -> 'en', a.third_text) ilike concat('%', :text, '%') or
                        coalesce(a.third_text_translations::hstore -> 'ru', a.third_text) ilike concat('%', :text, '%') or
                        coalesce(a.third_text_translations::hstore -> 'zh', a.third_text) ilike concat('%', :text, '%')
                    order by
                        CASE WHEN :sort = 'id' THEN a.id END,
                        CASE WHEN :sort = 'title' THEN coalesce(a.title_translations::hstore -> :lang, a.title) END,
                        CASE WHEN :sort = 'subtitle' THEN coalesce(a.subtitle_translations::hstore -> :lang, a.subtitle) END,
                        CASE WHEN :sort = 'third_text' THEN coalesce(a.third_text_translations::hstore -> :lang, a.third_text) END,
                        CASE WHEN :sort = 'creation_date' THEN a.creation_date END,
                        CASE WHEN :sort = 'expiration_date' THEN a.expiration_date END
            """, nativeQuery = true)
    List<AdvertisementWithTranslationsView> findAllWithTranslationsViewsByLang(
            @Param("lang") String lang,
            @Param("text") String text,
            @Param("sort") String sort);

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
            coalesce(
                a.third_text_translations -> :lang,
                a.third_text
            ) as third_text,
            a.image_url,
            a.link,
            a.is_big,
            a.expiration_date,
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
            coalesce(
                a.third_text_translations -> :lang,
                a.third_text
            ) as third_text,
            a.third_text_translations::text,
            a.image_url,
            a.link,
            a.is_big,
            a.expiration_date,
            a.creation_date,
            a.last_modification_date
            from advertisements a
            where a.id = :id
            """, nativeQuery = true)
    Optional<AdvertisementWithTranslationsView> findViewWithTranslationsByIdAndLang(@Param("id") Long id, @Param("lang") String lang);
}
