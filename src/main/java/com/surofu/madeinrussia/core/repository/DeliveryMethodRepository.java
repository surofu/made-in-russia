package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;

import java.util.List;
import java.util.Optional;

public interface DeliveryMethodRepository {
    List<DeliveryMethod> getAllDeliveryMethods();

    Optional<DeliveryMethod> getDeliveryMethodById(Long id);

    List<DeliveryMethod> getAllDeliveryMethodsByIds(List<Long> ids);

    Optional<Long> firstNotExists(List<Long> ids);
}
