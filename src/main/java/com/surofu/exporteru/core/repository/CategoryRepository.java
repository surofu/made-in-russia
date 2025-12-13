package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.category.CategorySlug;
import com.surofu.exporteru.infrastructure.persistence.category.CategoryView;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> getAll();

    Optional<Category> getById(Long id);

    Optional<Category> getBySlug(CategorySlug slug);

    List<Long> getCategoriesIdsByIds(List<Long> ids);

    List<Category> getCategoryL1AndL2();

    void save(Category category);

    Optional<Category> getCategoryBySlugWithChildren(CategorySlug slug);

    Boolean existsBySlug(CategorySlug slug);

    Boolean existsById(Long categoryId);

    void delete(Category category);

    // View

    List<CategoryView> getCategoryViewWithChildrenBySlugAndLang(String slug, String lang);
}
