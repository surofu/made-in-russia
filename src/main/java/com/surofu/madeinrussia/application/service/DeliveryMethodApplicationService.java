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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryMethodApplicationService implements DeliveryMethodService {

    private final DeliveryMethodRepository repository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "deliveryMethods",
            unless = "#result.getDeliveryMethodDtos().isEmpty()"
    )
    public GetDeliveryMethods.Result getDeliveryMethods() {
        List<DeliveryMethod> deliveryMethods = repository.getAllDeliveryMethods();
        List<DeliveryMethodDto> deliveryMethodDtos = new ArrayList<>(deliveryMethods.size());

        for (DeliveryMethod deliveryMethod : deliveryMethods) {
            deliveryMethodDtos.add(DeliveryMethodDto.of(deliveryMethod));
        }

        return GetDeliveryMethods.Result.success(deliveryMethodDtos);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "deliveryMethod",
            key = "#operation.getDeliveryMethodId()",
            unless = "#result instanceof T(com.surofu.madeinrussia.core.service.deliveryMethod.operation.GetDeliveryMethodById$Result$NotFound)"
    )
    public GetDeliveryMethodById.Result getDeliveryMethodById(GetDeliveryMethodById operation) {
        Optional<DeliveryMethod> deliveryMethod = repository.getDeliveryMethodById(operation.getDeliveryMethodId());
        Optional<DeliveryMethodDto> deliveryMethodDto = deliveryMethod.map(DeliveryMethodDto::of);

        if (deliveryMethodDto.isPresent()) {
            return GetDeliveryMethodById.Result.success(deliveryMethodDto.get());
        }

        return GetDeliveryMethodById.Result.notFound(operation.getDeliveryMethodId());
    }
}