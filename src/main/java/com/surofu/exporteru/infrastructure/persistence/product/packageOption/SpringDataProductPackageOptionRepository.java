package com.surofu.exporteru.infrastructure.persistence.product.packageOption;

import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProductPackageOptionRepository extends JpaRepository<ProductPackageOption, Long> {

    @Query(value = """
            select
            o.id,
            coalesce(
                o.name_translations::jsonb ->> :lang,
                o.name
            ) as name,
            o.price,
            o.price_unit,
            o.creation_date,
            o.last_modification_date
            from product_package_options o
            where o.product_id = :productId
            """, nativeQuery = true)
    List<ProductPackageOptionView> findAllViewsByProductIdAndLang(@Param("productId") Long productId, @Param("lang") String lang);

    @Query(value = """
            select
            o.id,
            coalesce(
                o.name_translations::jsonb ->> :lang,
                o.name
            ) as name,
            o.name_translations,
            o.price,
            o.price_unit,
            o.creation_date,
            o.last_modification_date
            from product_package_options o
            where o.product_id = :productId
            """, nativeQuery = true)
    List<ProductPackageOptionWithTranslationsView> findAllViewsWithTranslationsByProductIdAndLang(@Param("productId") Long productId, @Param("lang") String lang);

    List<ProductPackageOption> findAllByProductId(Long id);
}
