package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.repository.DeliveryMethodRepository;
import com.surofu.madeinrussia.core.service.deliveryMethod.DeliveryMethodService;
import com.surofu.madeinrussia.core.service.deliveryMethod.operation.GetDeliveryMethodById;
import com.surofu.madeinrussia.core.service.deliveryMethod.operation.GetDeliveryMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Application service implementation for delivery method operations.
 * Handles business logic for delivery method management including retrieval and caching.
 */
@Service
@RequiredArgsConstructor
public class DeliveryMethodApplicationService implements DeliveryMethodService {

    private final DeliveryMethodRepository repository;

    /**
     * Retrieves all available delivery methods.
     * Results are cached under 'deliveryMethods' cache namespace.
     *
     * @return GetDeliveryMethods.Result containing list of DeliveryMethodDto objects
     * @apiNote The cache has a default TTL of 24 hours
     * @see DeliveryMethodDto
     */
    @Override
    @Cacheable(
            value = "deliveryMethods",
            unless = "#result == null"
    )
    public GetDeliveryMethods.Result getDeliveryMethods() {
        List<DeliveryMethod> deliveryMethods = repository.getDeliveryMethods();
        List<DeliveryMethodDto> deliveryMethodDtos = new ArrayList<>(deliveryMethods.size());

        for (DeliveryMethod deliveryMethod : deliveryMethods) {
            deliveryMethodDtos.add(DeliveryMethodDto.of(deliveryMethod));
        }

        return GetDeliveryMethods.Result.success(deliveryMethodDtos);
    }

    /**
     * Retrieves a specific delivery method by its unique identifier.
     * Results are cached using the delivery method ID as the cache key.
     *
     * @param operation GetDeliveryMethodById operation containing the delivery method ID
     * @return GetDeliveryMethodById.Result containing either:
     *         - The found delivery method (wrapped in success)
     *         - Not-found status if delivery method doesn't exist
     * @throws IllegalArgumentException if operation or query is null
     * @apiNote Cache entries are automatically evicted when delivery methods are updated
     * @cacheEvict Corresponding cache entry is cleared when delivery method is updated
     */
    @Override
    @Cacheable(
            value = "deliveryMethod",
            key = "#operation.query.deliveryMethodId()",
            unless = "#result == null"
    )
    public GetDeliveryMethodById.Result getDeliveryMethodById(GetDeliveryMethodById operation) {
        Optional<DeliveryMethod> deliveryMethod = repository.getDeliveryMethodById(operation.getQuery().deliveryMethodId());
        Optional<DeliveryMethodDto> deliveryMethodDto = deliveryMethod.map(DeliveryMethodDto::of);

        if (deliveryMethodDto.isPresent()) {
            return GetDeliveryMethodById.Result.success(deliveryMethodDto.get());
        }

        return GetDeliveryMethodById.Result.notFound(operation.getQuery().deliveryMethodId());
    }
}