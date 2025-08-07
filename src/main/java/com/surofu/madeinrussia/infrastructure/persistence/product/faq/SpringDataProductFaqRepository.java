package com.surofu.madeinrussia.infrastructure.persistence.product.faq;

import com.surofu.madeinrussia.core.model.product.faq.ProductFaq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProductFaqRepository extends JpaRepository<ProductFaq, Long> {

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
            from product_faq f
            where f.product_id = :productId
            """, nativeQuery = true)
    List<ProductFaqView> findAllViewsByProductIdAndLang(@Param("productId") Long productId, @Param("lang") String lang);

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
            from product_faq f
            where f.product_id = :productId
            """, nativeQuery = true)
    List<ProductFaqWithTranslationsView> findAllViewsWithTranslationsByProductIdAndLang(@Param("productId") Long productId, @Param("lang") String lang);
}
