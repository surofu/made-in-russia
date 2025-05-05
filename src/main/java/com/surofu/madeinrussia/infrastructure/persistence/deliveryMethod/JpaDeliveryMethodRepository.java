package com.surofu.madeinrussia.infrastructure.persistence.deliveryMethod;

import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.repository.DeliveryMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of {@link DeliveryMethodRepository} interface.
 * Provides database access operations for {@link DeliveryMethod} entities using Spring Data JPA.
 *
 * <p>This repository handles all persistence operations for delivery methods including
 * retrieval by ID and fetching all available delivery methods.</p>
 */
@Repository
@RequiredArgsConstructor
public class JpaDeliveryMethodRepository implements DeliveryMethodRepository {

    private final SpringDataDeliveryMethodRepository repository;

    /**
     * Retrieves all delivery methods from the database.
     *
     * @return A list of all active delivery methods ordered by default sorting
     * @throws org.springframework.dao.DataAccessException if there is a database access problem
     * @apiNote Consider implementing pagination if the number of delivery methods grows large
     * @see DeliveryMethod
     */
    @Override
    public List<DeliveryMethod> getDeliveryMethods() {
        return repository.findAll();
    }

    /**
     * Finds a delivery method by its unique identifier.
     *
     * @param id The delivery method ID to search for (must not be {@code null})
     * @return An {@link Optional} containing the found delivery method if it exists,
     *         or empty {@link Optional} if no delivery method was found
     * @throws IllegalArgumentException if the id is {@code null}
     * @throws org.springframework.dao.DataAccessException if there is a database access problem
     * @see DeliveryMethod
     */
    @Override
    public Optional<DeliveryMethod> getDeliveryMethodById(Long id) {
        return repository.findById(id);
    }
}