package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTerm;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTermCode;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DeliveryTermRepository {
  List<DeliveryTerm> findAll();

  List<DeliveryTerm> getAllByIds(Collection<Long> deliveryTermIds);

  Optional<DeliveryTerm> findById(Long id);

  Optional<Long> firstNotExists(List<Long> deliveryTermIds);

  Boolean existsById(Long id);

  Boolean existsByCode(DeliveryTermCode code);

  DeliveryTerm save(DeliveryTerm deliveryTerm);

  void delete(DeliveryTerm deliveryTerm);
}
