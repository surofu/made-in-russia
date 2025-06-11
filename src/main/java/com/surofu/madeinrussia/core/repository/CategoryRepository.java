package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.category.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> getCategories();

    Optional<Category> getCategoryById(Long id);

    List<Category> getCategoriesByIds(List<Long> ids);
}
