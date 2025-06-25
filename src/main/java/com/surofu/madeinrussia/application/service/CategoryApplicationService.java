package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.CategoryDto;
import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import com.surofu.madeinrussia.core.service.category.CategoryService;
import com.surofu.madeinrussia.core.service.category.operation.GetAllCategories;
import com.surofu.madeinrussia.core.service.category.operation.GetCategories;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryById;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryBySlug;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryApplicationService implements CategoryService {

    private final CategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "AllCategories",
            unless = "#result.getCategoryDtos().isEmpty()"
    )
    public GetAllCategories.Result getAllCategories() {
        List<Category> categories = repository.getAllCategoriesWithParent();
        List<CategoryDto> categoryDtos = buildTree(categories);
        return GetAllCategories.Result.success(categoryDtos);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public GetCategoryById.Result getCategoryById(GetCategoryById operation) {
        Optional<Category> category = repository.getCategoryById(operation.getCategoryId());
        Optional<CategoryDto> categoryDto = category.map(CategoryDto::of);

        if (categoryDto.isPresent()) {
            return GetCategoryById.Result.success(categoryDto.get());
        }

        return GetCategoryById.Result.notFound(operation.getCategoryId());
    }

    @Override
    @Transactional(readOnly = true)
    public GetCategoryBySlug.Result getCategoryBySlug(GetCategoryBySlug operation) {
        Optional<Category> category = repository.getCategoryBySlug(operation.getCategorySlug());
        Optional<CategoryDto> categoryDto = category.map(CategoryDto::of);

        if (categoryDto.isPresent()) {
            return GetCategoryBySlug.Result.success(categoryDto.get());
        }

        return GetCategoryBySlug.Result.notFound(operation.getCategorySlug());
    }

    private List<CategoryDto> buildTree(List<Category> categories) {
        Map<Long, CategoryDto> dtoMap = new HashMap<>();
        List<CategoryDto> roots = new ArrayList<>();

        // Первый проход: создаем все DTO
        categories.forEach(category -> {
            CategoryDto dto = CategoryDto.ofWithoutChildren(category);
            dtoMap.put(category.getId(), dto);
        });

        // Второй проход: строим дерево
        categories.forEach(category -> {
            CategoryDto dto = dtoMap.get(category.getId());
            if (category.getParent() == null) {
                roots.add(dto);
            } else {
                CategoryDto parentDto = dtoMap.get(category.getParent().getId());
                if (parentDto != null) {
                    parentDto.getChildren().add(dto);
                }
            }
        });

        // Обновляем childrenCount для всех узлов
        dtoMap.values().forEach(dto ->
                dto.setChildrenCount((long) dto.getChildren().size()));

        return roots;
    }
}