package com.surofu.exporteru.infrastructure.persistence.okved;

import com.surofu.exporteru.core.model.okved.OkvedCategory;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataOkvedCategoryRepository extends JpaRepository<OkvedCategory, Long> {
  // Метод вернет список OkvedCategory для указанных categoryIds
  List<OkvedCategory> findByCategoryIdIn(Collection<Long> categoryIds);

  // Если нужно именно Map<Long, List<OkvedCategory>>
  // Можно использовать @Query с группировкой
  @Query("SELECT oc.category.id, oc FROM OkvedCategory oc WHERE oc.category.id IN :categoryIds")
  List<Object[]> findGroupedByCategoryIdIn(@Param("categoryIds") Collection<Long> categoryIds);

  // Или более удобный вариант - получить Map через default метод
  default Map<Long, List<OkvedCategory>> getAllByCategories(Collection<Long> categoryIds) {
    return findByCategoryIdIn(categoryIds).stream()
        .collect(Collectors.groupingBy(oc -> oc.getCategory().getId()));
  }
}
