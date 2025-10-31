package com.surofu.exporteru.infrastructure.persistence.okved;

import com.surofu.exporteru.core.model.okved.OkvedCategory;
import com.surofu.exporteru.core.repository.OkvedCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaOkvedCategoryRepository implements OkvedCategoryRepository {

    private final
    SpringDataOkvedCategoryRepository repository;

    @Override
    public List<OkvedCategory> getAll(List<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public List<OkvedCategory> getByCategoryId(Long categoryId) {
        return repository.findByCategoryId(categoryId);
    }

    @Override
    public void saveAll(Collection<OkvedCategory> okvedCategories) {
        repository.saveAll(okvedCategories);
    }

    @Override
    public void deleteAll(Collection<OkvedCategory> okvedCategories) {
        repository.deleteAll(okvedCategories);
    }

    @Override
    public List<OkvedCategoryView> getAllViews() {
        return repository.findAllViews();
    }
}
