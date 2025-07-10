package com.surofu.madeinrussia.infrastructure.persistence.deliveryMethod;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.repository.DeliveryMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaDeliveryMethodRepository implements DeliveryMethodRepository {

    private final SpringDataDeliveryMethodRepository repository;

    @Override
    public List<DeliveryMethod> getAllDeliveryMethods() {
        return repository.findAll();
    }

    @Override
    public List<DeliveryMethodView> getAllDeliveryMethodViewsByLang(String lang) {
        return repository.findAllViewsByLang(lang);
    }

    @Override
    public Optional<DeliveryMethod> getDeliveryMethodById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<DeliveryMethod> getAllDeliveryMethodsByIds(List<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public Optional<Long> firstNotExists(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Optional.empty();
        }
        return repository.firstNotExists(ids.toArray(new Long[0]));
    }
}