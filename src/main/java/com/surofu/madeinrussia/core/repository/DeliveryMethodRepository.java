package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.infrastructure.persistence.deliveryMethod.DeliveryMethodView;

import java.util.List;
import java.util.Optional;

public interface DeliveryMethodRepository {
    List<DeliveryMethodView> getAllDeliveryMethodViewsByLang(String lang);

    List<DeliveryMethodView> getAllDeliveryMethodViewsByProductIdLang(Long productId, String lang);

    Optional<DeliveryMethodView> getDeliveryMethodViewByIdAndLang(Long id, String lang);

    List<DeliveryMethod> getAllDeliveryMethodsByIds(List<Long> ids);

    Optional<Long> firstNotExists(List<Long> ids);
}
