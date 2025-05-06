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

@Service
@RequiredArgsConstructor
public class CategoryApplicationService implements CategoryService {

    private final CategoryRepository repository;

    @Override
    @Cacheable(
            value = "categories",
            unless = "#result.getCategoryDtos().isEmpty()"
    )
    public GetCategories.Result getCategories() {
        List<Category> categories = repository.getCategories();
        List<CategoryDto> categoryDtos = new ArrayList<>(categories.size());

        for (Category category : categories) {
            categoryDtos.add(CategoryDto.of(category));
        }

        return GetCategories.Result.success(categoryDtos);
    }

    @Override
    @Cacheable(
            value = "category",
            key = "#operation.query.categoryId()",
            unless = "#result instanceof T(com.surofu.madeinrussia.core.service.category.operation.GetCategoryById$Result$NotFound)"
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