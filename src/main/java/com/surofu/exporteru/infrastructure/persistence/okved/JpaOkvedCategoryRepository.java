package com.surofu.exporteru.infrastructure.persistence.okved;

import com.surofu.exporteru.core.model.okved.OkvedCategory;
import com.surofu.exporteru.core.repository.OkvedCategoryRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaOkvedCategoryRepository implements OkvedCategoryRepository {
  private final SpringDataOkvedCategoryRepository repository;

  @Override
  public Map<Long, List<OkvedCategory>> getAllByCategories(Collection<Long> ids) {
    return repository.getAllByCategories(ids);
  }
}
