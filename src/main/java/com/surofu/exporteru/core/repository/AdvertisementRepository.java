package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.advertisement.Advertisement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public interface AdvertisementRepository {
  List<Advertisement> getAll(Specification<Advertisement> specification, Sort sort);

  Optional<Advertisement> getById(Long id);

  void save(Advertisement advertisement);

  void delete(Advertisement advertisement);
}
