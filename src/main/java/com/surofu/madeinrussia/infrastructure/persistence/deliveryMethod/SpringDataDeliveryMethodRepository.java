package com.surofu.madeinrussia.infrastructure.persistence.deliveryMethod;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataDeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {

    @Query(value = """
            SELECT input.id
            FROM unnest(?1) WITH ORDINALITY AS input(id, ord)
            LEFT JOIN delivery_methods d ON d.id = input.id
            WHERE d.id IS NULL
            ORDER BY input.ord
            LIMIT 1
            """, nativeQuery = true)
    Optional<Long> firstNotExists(Long[] ids);

    @Query(value = """
        select
        dm.id,
        coalesce(
            dm.name_translations -> :lang,
            dm.name
        ) as name,
        dm.creation_date,
        dm.last_modification_date
        from delivery_methods dm
    """, nativeQuery = true)
    List<DeliveryMethodView> findAllViewsByLang(@Param("lang") String lang);

    @Query(value = """
            select
            dm.id,
            coalesce(
                dm.name_translations -> :lang,
                dm.name
            ) as name,
            dm.creation_date,
            dm.last_modification_date
            from delivery_methods dm
            where dm.id = :id
            """, nativeQuery = true)
    Optional<DeliveryMethodView> findByIdWithLang(@Param("id") Long id, @Param("lang") String lang);
}
