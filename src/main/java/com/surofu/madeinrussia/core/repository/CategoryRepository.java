package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.category.CategorySlug;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> getCategories();

    Optional<Category> getCategoryById(Long id);

    Optional<Category> getCategoryBySlug(CategorySlug slug);

    List<Long> getCategoriesIdsByIds(List<Long> ids);
}
