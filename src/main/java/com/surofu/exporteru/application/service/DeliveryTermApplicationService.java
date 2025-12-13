package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.dto.deliveryTerm.DeliveryTermDto;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTerm;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTermDescription;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTermName;
import com.surofu.exporteru.core.repository.DeliveryTermRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.deliveryTerm.DeliveryTermService;
import com.surofu.exporteru.core.service.deliveryTerm.operation.DeleteDeliveryTerm;
import com.surofu.exporteru.core.service.deliveryTerm.operation.GetAllDeliveryTerms;
import com.surofu.exporteru.core.service.deliveryTerm.operation.SaveDeliveryTerm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@RequiredArgsConstructor
public class DeliveryTermApplicationService implements DeliveryTermService {
  private final DeliveryTermRepository deliveryTermRepository;
  private final TranslationRepository translationRepository;

  @Override
  @Transactional(readOnly = true)
  public GetAllDeliveryTerms.Result getAll() {
    List<DeliveryTerm> deliveryTerms = deliveryTermRepository.findAll();
    List<DeliveryTermDto> dtos = deliveryTerms.stream().map(DeliveryTermDto::of).toList();
    return GetAllDeliveryTerms.Result.success(dtos);
  }

  @Override
  @Transactional
  public SaveDeliveryTerm.Result save(SaveDeliveryTerm operation) {
    try {
      if (operation.getId() != null && !deliveryTermRepository.existsById(operation.getId())) {
        return SaveDeliveryTerm.Result.notFound(operation.getId());
      }

      if (operation.getId() == null && deliveryTermRepository.existsByCode(operation.getCode())) {
        return SaveDeliveryTerm.Result.alreadyExistWithCode(operation.getCode());
      }

      DeliveryTerm deliveryTerm;

      if (operation.getId() != null) {
        deliveryTerm =
            deliveryTermRepository.findById(operation.getId()).orElse(new DeliveryTerm());
      } else {
        deliveryTerm = new DeliveryTerm();
      }

      deliveryTerm.setId(operation.getId());
      deliveryTerm.setCode(operation.getCode());

      Map<String, String> translationMap = new HashMap<>();
      if (!Objects.equals(deliveryTerm.getName(), operation.getName())) {
        translationMap.put("name", operation.getName().getValue());
      }
      if (!Objects.equals(deliveryTerm.getDescription(), operation.getDescription())) {
        translationMap.put("description", operation.getDescription().getValue());
      }
      Map<String, Map<String, String>> translationResult =
          translationRepository.expandMap(translationMap);

      if (!Objects.equals(deliveryTerm.getName(), operation.getName())) {
        deliveryTerm.setName(new DeliveryTermName(
            operation.getName().getValue(), translationResult.get("name")));
      }
      if (!Objects.equals(deliveryTerm.getDescription(), operation.getDescription())) {
        deliveryTerm.setDescription(new DeliveryTermDescription(
            operation.getDescription().getValue(), translationResult.get("description")));
      }

      DeliveryTerm savedDeliveryTerm = deliveryTermRepository.save(deliveryTerm);
      return SaveDeliveryTerm.Result.success(DeliveryTermDto.of(savedDeliveryTerm));
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return SaveDeliveryTerm.Result.saveError(e);
    }
  }

  @Override
  @Transactional
  public DeleteDeliveryTerm.Result delete(DeleteDeliveryTerm operation) {
    try {
      Optional<DeliveryTerm> deliveryTermOptional =
          deliveryTermRepository.findById(operation.getId());

      if (deliveryTermOptional.isEmpty()) {
        return DeleteDeliveryTerm.Result.notFound(operation.getId());
      }

      deliveryTermRepository.delete(deliveryTermOptional.get());
      return DeleteDeliveryTerm.Result.success(operation.getId());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return DeleteDeliveryTerm.Result.deleteError(e);
    }
  }
}
