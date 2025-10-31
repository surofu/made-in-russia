package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.okved.OkvedCategory;
import com.surofu.exporteru.infrastructure.persistence.okved.OkvedCategoryView;

import java.util.Collection;
import java.util.List;

public interface OkvedCategoryRepository {
    List<OkvedCategory> getAll(List<Long> ids);

    List<OkvedCategory> getByCategoryId(Long categoryId);

    void saveAll(Collection<OkvedCategory> okvedCategories);

    void deleteAll(Collection<OkvedCategory> okvedCategories);

    List<OkvedCategoryView> getAllViews();
}
