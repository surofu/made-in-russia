package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.CategoryDto;
import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import com.surofu.madeinrussia.core.service.category.CategoryService;
import com.surofu.madeinrussia.core.service.category.operation.GetCategories;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryById;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Application service implementation for category operations.
 * Handles business logic for category management including retrieval and caching.
 */
@Service
@RequiredArgsConstructor
public class CategoryApplicationService implements CategoryService {

    private final CategoryRepository repository;

    /**
     * Retrieves all available product categories.
     * Results are cached under 'categories' cache namespace.
     *
     * @return GetCategories.Result containing list of CategoryDto objects
     * @apiNote The cache is automatically refreshed when the underlying data changes
     * @see CategoryDto
     */
    @Override
    @Cacheable(
            value = "categories",
            unless = "#result == null"
    )
    public GetCategories.Result getCategories() {
        List<Category> categories = repository.getCategories();
        List<CategoryDto> categoryDtos = new ArrayList<>(categories.size());

        for (Category category : categories) {
            categoryDtos.add(CategoryDto.of(category));
        }

        return GetCategories.Result.success(categoryDtos);
    }

    /**
     * Retrieves a specific category by its unique identifier.
     * Results are cached using the category ID as the cache key.
     *
     * @param operation GetCategoryById operation containing the category ID
     * @return GetCategoryById.Result containing either the found category or not-found status
     * @throws IllegalArgumentException if the operation or query is null
     * @apiNote Cache entries are automatically evicted when categories are updated
     */
    @Override
    @Cacheable(
            value = "category",
            key = "#operation.query.categoryId()",
            unless = "#result == null"
    )
    public GetCategoryById.Result getCategoryById(GetCategoryById operation) {
        Optional<Category> category = repository.getCategoryById(operation.getQuery().categoryId());
        Optional<CategoryDto> categoryDto = category.map(CategoryDto::of);

        if (categoryDto.isPresent()) {
            return GetCategoryById.Result.success(categoryDto.get());
        }

        return GetCategoryById.Result.notFound(operation.getQuery().categoryId());
    }
}