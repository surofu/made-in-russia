package com.surofu.madeinrussia.infrastructure.persistence.deliveryMethod;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
