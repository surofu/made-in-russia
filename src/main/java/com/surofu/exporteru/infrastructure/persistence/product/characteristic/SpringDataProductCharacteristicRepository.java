package com.surofu.exporteru.infrastructure.persistence.product.characteristic;

import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProductCharacteristicRepository extends JpaRepository<ProductCharacteristic, Long> {

    @Query(value = """
        select
        c.id,
        coalesce(
            c.name_translations -> :lang,
            c.name
        ) as name,
        coalesce(
            c.value_translations -> :lang,
            c.value
        ) as value,
        c.creation_date,
        c.last_modification_date
        from product_characteristics c
        where c.product_id = :productId
    """, nativeQuery = true)
    List<ProductCharacteristicView> findAllByProductIdAndLang(@Param("productId") Long productId, @Param("lang") String lang);

    @Query(value = """
        select
        c.id,
        coalesce(
            c.name_translations -> :lang,
            c.name
        ) as name,
        c.name_translations::text,
        c.value,
        coalesce(
            c.value_translations -> :lang,
            c.value
        ) as value,
        c.value_translations::text,
        c.creation_date,
        c.last_modification_date
        from product_characteristics c
        where c.product_id = :productId
    """, nativeQuery = true)
    List<ProductCharacteristicWithTranslationsView> findAllWithTranslationsByProductIdAndLang(@Param("productId") Long productId, @Param("lang") String lang);

    List<ProductCharacteristic> getAllByProductId(Long id);
}
