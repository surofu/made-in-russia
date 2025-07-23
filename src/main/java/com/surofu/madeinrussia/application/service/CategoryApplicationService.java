package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.category.CategoryDto;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import com.surofu.madeinrussia.core.service.category.CategoryService;
import com.surofu.madeinrussia.core.service.category.operation.GetAllCategories;
import com.surofu.madeinrussia.core.service.category.operation.GetCategories;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryById;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryBySlug;
import com.surofu.madeinrussia.infrastructure.persistence.category.CategoryView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryApplicationService implements CategoryService {

    private final CategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public GetAllCategories.Result getAllCategories(GetAllCategories operation) {
        List<CategoryView> categories = repository.getAllCategoriesViewsByLang(operation.getLocale().getLanguage());
        List<CategoryDto> categoryDtos = buildTreeFromViews(categories);
        return GetAllCategories.Result.success(categoryDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCategories.Result getCategories(GetCategories operation) {
        List<CategoryView> categories = repository.getCategoryViewsL1AndL2ByLang(operation.getLocale().getLanguage());
        List<CategoryDto> categoryDtos = buildTreeFromViews(categories);
        return GetCategories.Result.success(categoryDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCategoryById.Result getCategoryById(GetCategoryById operation) {
        List<CategoryView> categories = repository.getCategoryViewWithChildrenByIdAndLang(operation.getCategoryId(), operation.getLocale().getLanguage());
        List<CategoryDto> categoryDtos = buildTreeFromViews(categories);

        if (categoryDtos.isEmpty()) {
            return GetCategoryById.Result.notFound(operation.getCategoryId());
        }

        return GetCategoryById.Result.success(categoryDtos.stream().findFirst().get());
    }

    @Override
    @Transactional(readOnly = true)
    public GetCategoryBySlug.Result getCategoryBySlug(GetCategoryBySlug operation) {
        List<CategoryView> categories = repository.getCategoryViewWithChildrenBySlugAndLang(operation.getCategorySlug().toString(), operation.getLocale().getLanguage());
        List<CategoryDto> categoryDtos = buildTreeFromViews(categories);

        if (categoryDtos.isEmpty()) {
            return GetCategoryBySlug.Result.notFound(operation.getCategorySlug());
        }

        return GetCategoryBySlug.Result.success(categoryDtos.stream().findFirst().get());
    }

    private List<CategoryDto> buildTreeFromViews(List<CategoryView> categories) {
        // Создаем Map для быстрого доступа к категориям по ID
        Map<Long, CategoryDto> dtoMap = new HashMap<>();

        // Сначала создаем все DTO без детей
        categories.forEach(view ->
                dtoMap.put(view.getId(), CategoryDto.ofWithoutChildren(view)));

        // Теперь строим дерево
        List<CategoryDto> roots = new ArrayList<>();

        categories.forEach(view -> {
            CategoryDto currentDto = dtoMap.get(view.getId());
            Long parentId = view.getParentCategoryId();

            if (parentId == null || !dtoMap.containsKey(parentId)) {
                // Это корневой элемент
                roots.add(currentDto);
            } else {
                // Добавляем к родителю
                CategoryDto parentDto = dtoMap.get(parentId);
                parentDto.getChildren().add(currentDto);
            }
        });

        // Рекурсивно обновляем childrenCount
        roots.forEach(this::updateChildrenCount);

        return roots;
    }

    private void updateChildrenCount(CategoryDto dto) {
        long count = dto.getChildren().size();
        for (CategoryDto child : dto.getChildren()) {
            updateChildrenCount(child);
            count += child.getChildrenCount(); // если нужно учитывать всех потомков
        }
        dto.setChildrenCount((long) dto.getChildren().size()); // или просто count для всех потомков
    }
}