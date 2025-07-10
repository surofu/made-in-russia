package com.surofu.madeinrussia.infrastructure.persistence.category;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.category.CategorySlug;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCategoryRepository implements CategoryRepository {

    private final SpringDataCategoryRepository repository;

    @Override
    public List<Category> getAllCategoriesWithParent() {
        return repository.findAllWithParent();
    }

    @Override
    public List<Category> getCategories() {
        return repository.findAll();
    }

    @Override
    public List<Category> getCategoriesL1AndL2() {
        return repository.findAllL1AndL2();
    }

    @Override
    public List<CategoryView> getCategoryViewsL1AndL2ByLang(String lang) {
        return repository.findAllViewsL1AndL2ByLang(lang);
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Category> getCategoryBySlug(CategorySlug slug) {
        return repository.findBySlug(slug);
    }

    @Override
    public List<Long> getCategoriesIdsByIds(List<Long> ids) {
        return repository.findAllIdsByIdWithAllChildren(ids);
    }
}