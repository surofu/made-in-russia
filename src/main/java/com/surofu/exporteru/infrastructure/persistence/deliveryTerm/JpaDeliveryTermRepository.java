package com.surofu.exporteru.infrastructure.persistence.deliveryTerm;

import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTerm;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTermCode;
import com.surofu.exporteru.core.repository.DeliveryTermRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDeliveryTermRepository implements DeliveryTermRepository {
  private final SpringDataDeliveryTermRepository repository;

  @Override
  public List<DeliveryTerm> findAll() {
    return repository.findAll();
  }

  @Override
  public List<DeliveryTerm> getAllByIds(Collection<Long> deliveryTermIds) {
    return repository.findAllById(deliveryTermIds);
  }

  @Override
  public List<DeliveryTerm> getAllByProductId(Long id) {
    return repository.findAllByProductsId(id);
  }

  @Override
  public Optional<DeliveryTerm> findById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Optional<Long> firstNotExists(List<Long> deliveryTermIds) {
    if (deliveryTermIds == null || deliveryTermIds.isEmpty()) {
      return Optional.empty();
    }
    return repository.firstNotExists(deliveryTermIds.toArray(new Long[0]));
  }

  @Override
  public Boolean existsById(Long id) {
    return repository.existsById(id);
  }

  @Override
  public Boolean existsByCode(DeliveryTermCode code) {
    return repository.existsByCode(code);
  }

  @Override
  public DeliveryTerm save(DeliveryTerm deliveryTerm) {
    return repository.save(deliveryTerm);
  }

  @Override
  public void delete(DeliveryTerm deliveryTerm) {
    repository.delete(deliveryTerm);
  }
}
