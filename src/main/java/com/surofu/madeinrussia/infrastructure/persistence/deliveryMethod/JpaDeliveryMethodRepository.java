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
    public List<DeliveryMethod> getDeliveryMethods() {
        return repository.findAll();
    }

    @Override
    public Optional<DeliveryMethod> getDeliveryMethodById(Long id) {
        return repository.findById(id);
    }
}
