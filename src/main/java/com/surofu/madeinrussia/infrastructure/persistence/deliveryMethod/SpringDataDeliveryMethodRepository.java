package com.surofu.madeinrussia.infrastructure.persistence.deliveryMethod;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {
}
