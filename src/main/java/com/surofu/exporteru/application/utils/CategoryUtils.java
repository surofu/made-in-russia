package com.surofu.exporteru.application.utils;

import com.surofu.exporteru.application.dto.category.CategoryDto;
import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.infrastructure.persistence.category.CategoryView;
import com.surofu.exporteru.infrastructure.persistence.category.CategoryWithProductsCountView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryUtils {

  public static List<CategoryDto> buildTree(List<Category> categories,
                                            List<CategoryWithProductsCountView> categoryWithProductsCountViews) {
    // Создаем Map для быстрого доступа к категориям по ID
    Map<Long, CategoryDto> dtoMap = new HashMap<>();

    // Сначала создаем все DTO без детей
    categories.forEach(c ->
        dtoMap.put(c.getId(), CategoryDto.ofWithoutChildren(c)));

    // Теперь строим дерево
    List<CategoryDto> roots = new ArrayList<>();
    Map<Long, Long> categoryWithProductsCountViewMap = categoryWithProductsCountViews.stream()
        .collect(Collectors.groupingBy(CategoryWithProductsCountView::getId,
            Collectors.summingLong(CategoryWithProductsCountView::getProductsCount)));

    categories.forEach(c -> {
      CategoryDto currentDto = dtoMap.get(c.getId());
      Long productsCount = categoryWithProductsCountViewMap.get(currentDto.getId());
      currentDto.setProductsCount(productsCount);
      Long parentId = c.getParent() != null ? c.getParent().getId() : null;

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
    roots.forEach(CategoryUtils::updateChildrenCount);

    return roots;
  }

  public static List<CategoryDto> buildTreeView(List<CategoryView> categories,
                                                List<CategoryWithProductsCountView> categoryWithProductsCountViews) {
    // Создаем Map для быстрого доступа к категориям по ID
    Map<Long, CategoryDto> dtoMap = new HashMap<>();

    // Сначала создаем все DTO без детей
    categories.forEach(c ->
        dtoMap.put(c.getId(), CategoryDto.ofWithoutChildren(c)));

    // Теперь строим дерево
    List<CategoryDto> roots = new ArrayList<>();
    Map<Long, Long> categoryWithProductsCountViewMap = categoryWithProductsCountViews.stream()
        .collect(Collectors.groupingBy(CategoryWithProductsCountView::getId,
            Collectors.summingLong(CategoryWithProductsCountView::getProductsCount)));

    categories.forEach(c -> {
      CategoryDto currentDto = dtoMap.get(c.getId());
      Long productsCount = categoryWithProductsCountViewMap.get(currentDto.getId());
      currentDto.setProductsCount(productsCount);
      Long parentId = c.getParentCategoryId();

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
    roots.forEach(CategoryUtils::updateChildrenCount);

    return roots;
  }

  private static void updateChildrenCount(CategoryDto dto) {
    for (CategoryDto child : dto.getChildren()) {
      updateChildrenCount(child);
    }
    dto.setChildrenCount((long) dto.getChildren().size());
  }
}
