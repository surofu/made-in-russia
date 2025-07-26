package com.surofu.madeinrussia.infrastructure.persistence.faq;

import com.surofu.madeinrussia.core.model.faq.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataFaqRepository extends JpaRepository<Faq, Long> {

    @Query(value = """
            select
            f.id,
            coalesce(
                f.question_translations -> :lang,
                f.question
            ) as question,
            coalesce(
                f.answer_translations -> :lang,
                f.answer
            ) as answer,
            f.creation_date,
            f.last_modification_date
            from faq f
            order by f.creation_date desc
            """, nativeQuery = true)
    List<FaqView> findAllViewsByLang(@Param("lang") String lang);

    @Query(value = """
            select
            f.id,
            coalesce(
                f.question_translations -> :lang,
                f.question
            ) as question,
            coalesce(
                f.answer_translations -> :lang,
                f.answer
            ) as answer,
            f.creation_date,
            f.last_modification_date
            from faq f
            where f.id = :id
            """, nativeQuery = true)
    Optional<FaqView> findViewByIdAndLang(@Param("id") Long id, @Param("lang") String lang);

    @Query(value = """
            select
            f.id,
            coalesce(
                f.question_translations -> :lang,
                f.question
            ) as question,
            f.question_translations::text,
            coalesce(
                f.answer_translations -> :lang,
                f.answer
            ) as answer,
            f.answer_translations::text,
            f.creation_date,
            f.last_modification_date
            from faq f
            where f.id = :id
            """, nativeQuery = true)
    Optional<FaqWithTranslationsView> findViewWithTranslationsByIdAndLang(@Param("id") Long id, @Param("lang") String lang);
}
