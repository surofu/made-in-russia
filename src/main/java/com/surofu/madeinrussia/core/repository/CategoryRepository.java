package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.category.CategorySlug;
import com.surofu.madeinrussia.infrastructure.persistence.category.CategoryView;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> getAllCategoriesWithParent();

    List<Category> getCategories();

    List<Category> getCategoriesL1AndL2();

    List<CategoryView> getCategoryViewsL1AndL2ByLang(String lang);

    Optional<Category> getCategoryById(Long id);

    Optional<Category> getCategoryBySlug(CategorySlug slug);

    List<Long> getCategoriesIdsByIds(List<Long> ids);
}
