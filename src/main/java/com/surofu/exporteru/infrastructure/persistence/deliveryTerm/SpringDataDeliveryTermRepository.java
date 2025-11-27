package com.surofu.exporteru.infrastructure.persistence.deliveryTerm;

import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTerm;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTermCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataDeliveryTermRepository extends JpaRepository<DeliveryTerm, Long> {
  @Query(value = """
            SELECT input.id
            FROM unnest(?1) WITH ORDINALITY AS input(id, ord)
            LEFT JOIN delivery_terms d ON d.id = input.id
            WHERE d.id IS NULL
            ORDER BY input.ord
            LIMIT 1
            """, nativeQuery = true)
  Optional<Long> firstNotExists(Long[] array);

  Boolean existsByCode(DeliveryTermCode code);

  List<DeliveryTerm> findAllByProductsId(Long id);
}
