package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.okved.OkvedCategory;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface OkvedCategoryRepository {
  Map<Long, List<OkvedCategory>> getAllByCategories(Collection<Long> ids);
}
