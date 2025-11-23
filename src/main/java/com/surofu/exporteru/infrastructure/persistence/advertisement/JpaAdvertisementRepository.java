package com.surofu.exporteru.infrastructure.persistence.advertisement;

import com.surofu.exporteru.core.model.advertisement.Advertisement;
import com.surofu.exporteru.core.repository.AdvertisementRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaAdvertisementRepository implements AdvertisementRepository {

  private final SpringDataAdvertisementRepository repository;

  @Override
  public List<Advertisement> getAll(Specification<Advertisement> specification, Sort sort) {
    return repository.findAll(specification, sort);
  }

  @Override
  public Optional<Advertisement> getById(Long id) {
    return repository.findById(id);
  }

  @Override
  public void save(Advertisement advertisement) {
    repository.save(advertisement);
  }

  @Override
  public void delete(Advertisement advertisement) {
    repository.delete(advertisement);
  }
}
