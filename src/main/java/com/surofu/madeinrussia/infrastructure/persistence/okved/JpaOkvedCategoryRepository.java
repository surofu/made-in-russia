package com.surofu.madeinrussia.infrastructure.persistence.okved;

import com.surofu.madeinrussia.core.model.okved.OkvedCategory;
import com.surofu.madeinrussia.core.repository.OkvedCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaOkvedCategoryRepository implements OkvedCategoryRepository {

    private final SpringDataOkvedCategoryRepository repository;

    @Override
    public List<OkvedCategory> getById(Long categoryId) {
        return repository.findByCategory_Id(categoryId);
    }

    @Override
    public List<OkvedCategory> getByIds(List<Long> ids) {
        return repository.findByCategory_Ids(ids);
    }
}
