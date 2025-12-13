package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.cache.DeliveryMethodsCacheManager;
import com.surofu.exporteru.application.cache.DeliveryMethodsListCacheManager;
import com.surofu.exporteru.application.dto.DeliveryMethodDto;
import com.surofu.exporteru.core.repository.DeliveryMethodRepository;
import com.surofu.exporteru.core.service.deliveryMethod.DeliveryMethodService;
import com.surofu.exporteru.core.service.deliveryMethod.operation.GetDeliveryMethodById;
import com.surofu.exporteru.core.service.deliveryMethod.operation.GetDeliveryMethods;
import com.surofu.exporteru.infrastructure.persistence.deliveryMethod.DeliveryMethodView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryMethodApplicationService implements DeliveryMethodService {
  private final DeliveryMethodRepository repository;
  private final DeliveryMethodsCacheManager deliveryMethodsCacheManager;
  private final DeliveryMethodsListCacheManager deliveryMethodsListCacheManager;

  @Override
  @Transactional(readOnly = true)
  public GetDeliveryMethods.Result getDeliveryMethods(GetDeliveryMethods operation) {
    // Check cache
    List<DeliveryMethodDto> cachedList =
        deliveryMethodsListCacheManager.getDeliveryMethod(operation.getLocale().toString());

    if (cachedList != null) {
      return GetDeliveryMethods.Result.success(cachedList);
    }

    // Process
    List<DeliveryMethodView> deliveryMethods =
        repository.getAllDeliveryMethodViewsByLang(operation.getLocale().getLanguage());
    List<DeliveryMethodDto> deliveryMethodDtos = new ArrayList<>(deliveryMethods.size());

    for (DeliveryMethodView deliveryMethod : deliveryMethods) {
      deliveryMethodDtos.add(DeliveryMethodDto.of(deliveryMethod));
    }

    try {
      deliveryMethodsListCacheManager.setDeliveryMethod(operation.getLocale().toString(),
          deliveryMethodDtos);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return GetDeliveryMethods.Result.success(deliveryMethodDtos);
  }

  @Override
  @Transactional(readOnly = true)
  public GetDeliveryMethodById.Result getDeliveryMethodById(GetDeliveryMethodById operation) {
    // Check cache
    String hash = operation.getDeliveryMethodId() + operation.getLocale().toString();
    DeliveryMethodDto cachedDto = deliveryMethodsCacheManager.getDeliveryMethod(hash);
    if (cachedDto != null) {
      return GetDeliveryMethodById.Result.success(cachedDto);
    }

    // Process
    Optional<DeliveryMethodView> deliveryMethod = repository.getDeliveryMethodViewByIdAndLang(
        operation.getDeliveryMethodId(), operation.getLocale().getLanguage());
    Optional<DeliveryMethodDto> deliveryMethodDto = deliveryMethod.map(DeliveryMethodDto::of);

    if (deliveryMethodDto.isEmpty()) {
      return GetDeliveryMethodById.Result.notFound(operation.getDeliveryMethodId());
    }

    try {
      deliveryMethodsCacheManager.setDeliveryMethod(hash, deliveryMethodDto.get());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return GetDeliveryMethodById.Result.success(deliveryMethodDto.get());
  }
}