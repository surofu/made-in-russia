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
    public List<String> getOkvedCategoryIdsBySlug(CategorySlug slug) {
        return repository.findOkvedCategoryIdsByCategoryId(slug);
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return repository.findById(id);
    }
    
    @Override
    public List<Long> getCategoriesIdsByIds(List<Long> ids) {
        return repository.findAllIdsByIdWithAllChildren(ids);
    }

    @Override
    public void save(Category category) {
        repository.save(category);
    }

    @Override
    public Boolean existsBySlug(CategorySlug slug) {
        return repository.existsBySlug(slug);
    }

    @Override
    public void delete(Category category) {
        repository.delete(category);
    }

    // View

    @Override
    public List<CategoryView> getAllCategoriesViewsByLang(String lang) {
        return repository.findAllCategoryViewsByLang(lang);
    }

    @Override
    public List<CategoryView> getCategoryViewsL1AndL2ByLang(String lang) {
        return repository.findAllViewsL1AndL2ByLang(lang);
    }

    @Override
    public Optional<CategoryView> getCategoryViewByIdAndLang(Long id, String lang) {
        return repository.findViewByIdAndLang(id, lang);
    }

    @Override
    public List<CategoryView> getCategoryViewWithChildrenByIdAndLang(Long id, String lang) {
        return repository.findCategoryWithChildrenViewByIdAndLang(id, lang);
    }

    @Override
    public List<CategoryView> getCategoryViewWithChildrenBySlugAndLang(String slug, String lang) {
        return repository.findCategoryWithChildrenViewBySlugAndLang(slug, lang);
    }
}