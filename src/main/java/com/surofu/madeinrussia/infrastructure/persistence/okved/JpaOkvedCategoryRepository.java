package com.surofu.madeinrussia.infrastructure.persistence.okved;

import com.surofu.madeinrussia.core.model.okved.OkvedCategory;
import com.surofu.madeinrussia.core.repository.OkvedCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaOkvedCategoryRepository implements OkvedCategoryRepository {

    private final SpringDataOkvedCategoryRepository repository;

    @Override
    public List<OkvedCategory> getByCategoryId(Long categoryId) {
        return repository.findByCategory_Id(categoryId);
    }

    @Override
    public void saveAll(Collection<OkvedCategory> okvedCategories) {
        repository.saveAll(okvedCategories);
    }

    @Override
    public void deleteAll(Collection<OkvedCategory> okvedCategories) {
        repository.deleteAll(okvedCategories);
    }
}
