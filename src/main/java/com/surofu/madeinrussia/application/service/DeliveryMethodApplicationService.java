package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.repository.DeliveryMethodRepository;
import com.surofu.madeinrussia.core.service.deliveryMethod.DeliveryMethodService;
import com.surofu.madeinrussia.core.service.deliveryMethod.operation.GetDeliveryMethodById;
import com.surofu.madeinrussia.core.service.deliveryMethod.operation.GetDeliveryMethods;
import com.surofu.madeinrussia.infrastructure.persistence.deliveryMethod.DeliveryMethodView;
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
    public GetDeliveryMethods.Result getDeliveryMethods(GetDeliveryMethods operation) {
        List<DeliveryMethodView> deliveryMethods = repository.getAllDeliveryMethodViewsByLang(operation.getLocale().getLanguage());
        List<DeliveryMethodDto> deliveryMethodDtos = new ArrayList<>(deliveryMethods.size());

        for (DeliveryMethodView deliveryMethod : deliveryMethods) {
            deliveryMethodDtos.add(DeliveryMethodDto.of(deliveryMethod));
        }

        return GetDeliveryMethods.Result.success(deliveryMethodDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public GetDeliveryMethodById.Result getDeliveryMethodById(GetDeliveryMethodById operation) {
        Optional<DeliveryMethodView> deliveryMethod = repository.getDeliveryMethodViewByIdWithLang(
                operation.getDeliveryMethodId(), operation.getLocale().getLanguage());
        Optional<DeliveryMethodDto> deliveryMethodDto = deliveryMethod.map(DeliveryMethodDto::of);

        if (deliveryMethodDto.isPresent()) {
            return GetDeliveryMethodById.Result.success(deliveryMethodDto.get());
        }

        return GetDeliveryMethodById.Result.notFound(operation.getDeliveryMethodId());
    }
}