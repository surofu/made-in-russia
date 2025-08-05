package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.category.CategorySlug;
import com.surofu.madeinrussia.infrastructure.persistence.category.CategoryView;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Optional<Category> getCategoryById(Long id);

    List<Long> getCategoriesIdsByIds(List<Long> ids);

    List<String> getOkvedCategoryIdsBySlug(CategorySlug slug);

    void save(Category category);

    Boolean existsBySlug(CategorySlug slug);

    void delete(Category category);

    // View

    List<CategoryView> getAllCategoriesViewsByLang(String lang);

    List<CategoryView> getCategoryViewsL1AndL2ByLang(String lang);

    List<CategoryView> getCategoryViewWithChildrenByIdAndLang(Long id, String lang);

    List<CategoryView> getCategoryViewWithChildrenBySlugAndLang(String slug, String lang);

    Optional<CategoryView> getCategoryViewByIdAndLang(Long id, String lang);
}
