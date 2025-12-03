package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.cache.CategoryCacheManager;
import com.surofu.exporteru.application.cache.CategoryListCacheManager;
import com.surofu.exporteru.application.cache.GeneralCacheService;
import com.surofu.exporteru.application.dto.category.CategoryDto;
import com.surofu.exporteru.application.enums.FileStorageFolders;
import com.surofu.exporteru.application.exception.EmptyTranslationException;
import com.surofu.exporteru.application.utils.CategoryUtils;
import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.category.CategoryDescription;
import com.surofu.exporteru.core.model.category.CategoryIconUrl;
import com.surofu.exporteru.core.model.category.CategoryImageUrl;
import com.surofu.exporteru.core.model.category.CategoryLabel;
import com.surofu.exporteru.core.model.category.CategoryMetaDescription;
import com.surofu.exporteru.core.model.category.CategoryName;
import com.surofu.exporteru.core.model.category.CategorySlug;
import com.surofu.exporteru.core.model.category.CategoryTitle;
import com.surofu.exporteru.core.repository.CategoryRepository;
import com.surofu.exporteru.core.repository.FileStorageRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.category.CategoryService;
import com.surofu.exporteru.core.service.category.operation.CreateCategory;
import com.surofu.exporteru.core.service.category.operation.DeleteCategoryById;
import com.surofu.exporteru.core.service.category.operation.GetAllCategories;
import com.surofu.exporteru.core.service.category.operation.GetCategories;
import com.surofu.exporteru.core.service.category.operation.GetCategoryById;
import com.surofu.exporteru.core.service.category.operation.GetCategoryBySlug;
import com.surofu.exporteru.core.service.category.operation.UpdateCategoryById;
import com.surofu.exporteru.infrastructure.persistence.category.CategoryView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryApplicationService implements CategoryService {
  private final CategoryRepository categoryRepository;
  private final TranslationRepository translationRepository;
  private final FileStorageRepository fileStorageRepository;
  private final CategoryCacheManager categoryCacheManager;
  private final CategoryListCacheManager categoryListCacheManager;
  private final GeneralCacheService generalCacheService;

  @Override
  @Transactional(readOnly = true)
  public GetAllCategories.Result getAllCategories(GetAllCategories operation) {
    // Check cache
    List<CategoryDto> cachedCategoryDtos =
        categoryListCacheManager.getAllByLocale("ALL_" + operation.getLocale().getLanguage());
    if (cachedCategoryDtos != null) {
      return GetAllCategories.Result.success(cachedCategoryDtos);
    }

    // Process
    List<Category> categories = categoryRepository.getAll();
    List<CategoryDto> categoryDtos = CategoryUtils.buildTree(categories);

    try {
      categoryListCacheManager.setAllByLocale("ALL_" + operation.getLocale().getLanguage(),
          categoryDtos);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return GetAllCategories.Result.success(categoryDtos);
  }

  @Override
  @Transactional(readOnly = true)
  public GetCategories.Result getCategories(GetCategories operation) {
    // Check cache
    List<CategoryDto> cachedCategoryDtos =
        categoryListCacheManager.getAllByLocale(operation.getLocale().getLanguage());
    if (cachedCategoryDtos != null) {
      return GetCategories.Result.success(cachedCategoryDtos);
    }

    // Process
    List<Category> categories = categoryRepository.getCategoryL1AndL2();
    List<CategoryDto> categoryDtos = CategoryUtils.buildTree(categories);

    try {
      categoryListCacheManager.setAllByLocale(operation.getLocale().getLanguage(), categoryDtos);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return GetCategories.Result.success(categoryDtos);
  }

  @Override
  @Transactional(readOnly = true)
  public GetCategoryById.Result getCategoryById(GetCategoryById operation) {
    // Check cache
    CategoryDto cachedCategoryDto =
        categoryCacheManager.getCategory(operation.getCategoryId(), operation.getLocale());

    if (cachedCategoryDto != null) {
      return GetCategoryById.Result.success(cachedCategoryDto);
    }

    // Process
    Optional<Category> categoryOptional = categoryRepository.getById(operation.getCategoryId());

    if (categoryOptional.isEmpty()) {
      return GetCategoryById.Result.notFound(operation.getCategoryId());
    }

    Category category = categoryOptional.get();
    CategoryDto categoryDto = CategoryDto.of(category);

    try {
      categoryCacheManager.setCategory(operation.getCategoryId(), operation.getLocale(),
          categoryDto);
      categoryCacheManager.setCategory(categoryDto.getSlug(), operation.getLocale(), categoryDto);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return GetCategoryById.Result.success(categoryDto);
  }

  @Override
  @Transactional(readOnly = true)
  public GetCategoryBySlug.Result getCategoryBySlug(GetCategoryBySlug operation) {
    // Check cache
    CategoryDto cachedCategoryDto =
        categoryCacheManager.getCategory(operation.getCategorySlug().toString(),
            operation.getLocale());

    if (cachedCategoryDto != null) {
      return GetCategoryBySlug.Result.success(cachedCategoryDto);
    }

    // Process
    if (categoryRepository.getBySlug(operation.getCategorySlug()).isEmpty()) {
      return GetCategoryBySlug.Result.notFound(operation.getCategorySlug());
    }

    List<CategoryView> categories =
        categoryRepository.getCategoryViewWithChildrenBySlugAndLang(
            operation.getCategorySlug().getValue(), operation.getLocale().getLanguage());

    List<CategoryDto> categoryDtos = CategoryUtils.buildTreeView(categories);
    CategoryDto categoryDto = categoryDtos.get(0);

    try {
      categoryCacheManager.setCategory(operation.getCategorySlug().toString(),
          operation.getLocale(), categoryDto);
      categoryCacheManager.setCategory(categoryDto.getId().toString(), operation.getLocale(),
          categoryDto);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return GetCategoryBySlug.Result.success(categoryDto);
  }

  @Override
  @Transactional
  public CreateCategory.Result createCategory(CreateCategory operation) {
    // Валидация родительской категории и уровня
    Result<CategoryLevelInfo> levelInfoResult = validateParentAndLevel(operation.getParentId());
    if (!levelInfoResult.success()) {
      return CreateCategory.Result.parentNotFound(operation.getParentId());
    }

    CategoryLevelInfo levelInfo = levelInfoResult.data();
    CategorySlug slugWithLevel =
        new CategorySlug(operation.getSlug().toString(), levelInfo.parentCategoryLevel());

    // Проверка уникальности slug
    if (categoryRepository.existsBySlug(slugWithLevel)) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return CreateCategory.Result.slugAlreadyExists(slugWithLevel);
    }

    // Создание категории
    Category category = buildCategory(operation, levelInfo.parentCategory(), slugWithLevel);

    // Сохранение переводов
    Result<Category> translationResult = saveTranslations(category, operation);
    if (!translationResult.success()) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return handleTranslationError(translationResult.error());
    }

    // Загрузка изображений
    Result<Category> imageResult = processImages(category, operation);
    if (!imageResult.success()) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return handleImageError(imageResult.error());
    }

    try {
      categoryRepository.save(category);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return CreateCategory.Result.saveError(category.getSlug(), e);
    } finally {
      clearCaches();
    }

    return CreateCategory.Result.success(category.getSlug());
  }

  @Override
  @Transactional
  public UpdateCategoryById.Result updateCategoryById(UpdateCategoryById operation) {
    // Валидация входных данных
    if (operation == null || operation.getId() == null) {
      throw new IllegalArgumentException("Operation or category ID cannot be null");
    }

    // Поиск категории
    Optional<Category> categoryOptional = categoryRepository.getById(operation.getId());
    if (categoryOptional.isEmpty()) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return UpdateCategoryById.Result.notFound(operation.getId());
    }

    Category category = categoryOptional.get();

    // Валидация родительской категории и уровня
    Result<CategoryLevelInfo> levelInfoResult = validateParentAndLevel(operation.getParentId());
    if (!levelInfoResult.success()) {
      return UpdateCategoryById.Result.parentNotFound(operation.getParentId());
    }

    CategoryLevelInfo levelInfo = levelInfoResult.data();
    CategorySlug slugWithLevel =
        new CategorySlug(operation.getSlug().toString(), levelInfo.parentCategoryLevel());

    // Проверка уникальности slug (если изменился)
    if (!category.getSlug().getValue().equals(slugWithLevel.getValue()) &&
        categoryRepository.existsBySlug(slugWithLevel)) {
      return UpdateCategoryById.Result.slugAlreadyExists(slugWithLevel);
    }

    // Обновление основных полей
    updateCategoryFields(category, operation, levelInfo.parentCategory(), slugWithLevel);

    // Сохранение переводов
    Result<Category> translationResult = saveTranslations(category, operation);
    if (!translationResult.success()) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return UpdateCategoryById.Result.translationError(translationResult.error());
    }

    // Обработка изображений
    UpdateCategoryById.Result imageResult = processCategoryImage(category, operation);
    if (!(imageResult instanceof UpdateCategoryById.Result.Success)) {
      return imageResult;
    }
    UpdateCategoryById.Result iconResult = processCategoryIcon(category, operation);
    if (!(iconResult instanceof UpdateCategoryById.Result.Success)) {
      return iconResult;
    }

    // Сохранение и очистка кэша
    try {
      categoryRepository.save(category);
      return UpdateCategoryById.Result.success(category.getSlug());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return UpdateCategoryById.Result.saveError(category.getSlug(), e);
    } finally {
      clearCaches();
    }
  }

// ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

  /**
   * Валидация родительской категории и расчет уровня
   */
  private Result<CategoryLevelInfo> validateParentAndLevel(Long parentId) {
    if (parentId == null) {
      return Result.success(new CategoryLevelInfo(null, 1));
    }

    Optional<Category> parentCategoryOptional = categoryRepository.getById(parentId);
    if (parentCategoryOptional.isEmpty()) {
      return Result.error("Parent category not found");
    }

    Category parentCategory = parentCategoryOptional.get();
    try {
      int parentCategoryLevel = 1 + Integer.parseInt(
          parentCategory.getSlug().getValue().split("_")[0].split("l")[1]
      );
      return Result.success(new CategoryLevelInfo(parentCategory, parentCategoryLevel));
    } catch (NumberFormatException e) {
      return Result.error("Parent slug level parse error");
    }
  }

  /**
   * Создание новой категории
   */
  private Category buildCategory(CreateCategory operation, Category parentCategory,
                                 CategorySlug slug) {
    Category category = new Category();
    category.setParent(parentCategory);
    category.setName(operation.getName());
    category.setTitle(operation.getTitle());
    category.setLabel(operation.getLabel());
    category.setDescription(operation.getDescription());
    category.setMetaDescription(operation.getMetaDescription());
    category.setSlug(slug);
    return category;
  }

  /**
   * Обновление полей существующей категории
   */
  private void updateCategoryFields(Category category, UpdateCategoryById operation,
                                    Category parentCategory, CategorySlug slug) {
    category.setParent(parentCategory);
    category.setName(operation.getName());
    category.setTitle(operation.getTitle());
    category.setLabel(operation.getLabel());
    category.setDescription(operation.getDescription());
    category.setMetaDescription(operation.getMetaDescription());
    category.setSlug(slug);
  }

  /**
   * Сохранение переводов для категории
   */
  private Result<Category> saveTranslations(Category category, Object operation) {
    try {
      Map<String, String> nameTranslations = getNameTranslations(operation);
      Map<String, String> titleTranslations = getTitleTranslations(operation);
      Map<String, String> labelTranslations = getLabelTranslations(operation);
      Map<String, String> descriptionTranslations = getDescriptionTranslations(operation);
      Map<String, String> metaDescriptionTranslations = getMetaDescriptionTranslations(operation);

      category.setName(new CategoryName(
          category.getName().getValue(),
          translationRepository.expand(nameTranslations)
      ));
      category.setTitle(new CategoryTitle(
          category.getTitle().getValue(),
          translationRepository.expand(titleTranslations)
      ));
      category.setLabel(new CategoryLabel(
          category.getLabel().getValue(),
          translationRepository.expand(labelTranslations)
      ));

      if (descriptionTranslations != null && !descriptionTranslations.isEmpty()) {
        category.setDescription(new CategoryDescription(
            category.getDescription().getValue(),
            translationRepository.expand(descriptionTranslations)
        ));
      }
      if (metaDescriptionTranslations != null && !metaDescriptionTranslations.isEmpty()) {
        category.setMetaDescription(new CategoryMetaDescription(
            category.getMetaDescription().getValue(),
            translationRepository.expand(metaDescriptionTranslations)
        ));
      }

      return Result.success(category);
    } catch (Exception e) {
      return Result.error(e);
    }
  }

  // Вспомогательные методы для получения переводов из разных операций
  private Map<String, String> getNameTranslations(Object operation) {
    if (operation instanceof CreateCategory createOp) {
      return createOp.getName().getTranslations();
    } else if (operation instanceof UpdateCategoryById updateOp) {
      return updateOp.getName().getTranslations();
    }
    throw new IllegalArgumentException("Unsupported operation type");
  }

  private Map<String, String> getTitleTranslations(Object operation) {
    if (operation instanceof CreateCategory createOp) {
      Map<String, String> nameTranslations = createOp.getName().getTranslations();
      return createOp.getTitle().getTranslations() != null ?
          createOp.getTitle().getTranslations() : nameTranslations;
    } else if (operation instanceof UpdateCategoryById updateOp) {
      return updateOp.getTitle().getTranslations();
    }
    throw new IllegalArgumentException("Unsupported operation type");
  }

  private Map<String, String> getLabelTranslations(Object operation) {
    if (operation instanceof CreateCategory createOp) {
      Map<String, String> nameTranslations = createOp.getName().getTranslations();
      return createOp.getLabel().getTranslations() != null ?
          createOp.getLabel().getTranslations() : nameTranslations;
    } else if (operation instanceof UpdateCategoryById updateOp) {
      return updateOp.getLabel().getTranslations();
    }
    throw new IllegalArgumentException("Unsupported operation type");
  }

  private Map<String, String> getDescriptionTranslations(Object operation) {
    if (operation instanceof CreateCategory createOp) {
      return createOp.getDescription().getTranslations();
    } else if (operation instanceof UpdateCategoryById updateOp) {
      return updateOp.getDescription().getTranslations();
    }
    throw new IllegalArgumentException("Unsupported operation type");
  }

  private Map<String, String> getMetaDescriptionTranslations(Object operation) {
    if (operation instanceof CreateCategory createOp) {
      return createOp.getMetaDescription().getTranslations();
    } else if (operation instanceof UpdateCategoryById updateOp) {
      return updateOp.getMetaDescription().getTranslations();
    }
    throw new IllegalArgumentException("Unsupported operation type");
  }

  /**
   * Обработка изображений категории
   */
  private Result<Category> processImages(Category category, CreateCategory operation) {
    try {
      // Обработка основного изображения
      MultipartFile imageFile = operation.getImageFile();
      if (imageFile != null && !imageFile.isEmpty()) {
        String url = fileStorageRepository.uploadImageToFolder(
            imageFile, FileStorageFolders.CATEGORY_IMAGES.getValue()
        );
        category.setImageUrl(new CategoryImageUrl(url));
      }

      // Обработка иконки
      MultipartFile iconFile = operation.getIconFile();
      if (iconFile != null && !iconFile.isEmpty()) {
        String url = fileStorageRepository.uploadImageToFolder(
            iconFile, FileStorageFolders.CATEGORY_ICONS.getValue()
        );
        category.setIconUrl(new CategoryIconUrl(url));
      }

      return Result.success(category);
    } catch (Exception e) {
      return Result.error(e);
    }
  }

  /**
   * Очистка кэшей
   */
  private void clearCaches() {
    categoryCacheManager.clear();
    categoryListCacheManager.clearAll();
    generalCacheService.clear();
  }

  // Обработчики ошибок для разных операций
  private CreateCategory.Result handleTranslationError(Exception e) {
    if (e instanceof EmptyTranslationException) {
      return CreateCategory.Result.emptyTranslations();
    }
    return CreateCategory.Result.translationError(e);
  }

  private CreateCategory.Result handleImageError(Exception e) {
    return CreateCategory.Result.uploadImageError(e);
  }

  private UpdateCategoryById.Result processCategoryImage(Category category,
                                                         UpdateCategoryById operation) {
    String currentImageUrl = category.getImageUrl() != null ?
        StringUtils.trimToNull(category.getImageUrl().toString()) : null;

    // Удаление изображения если не нужно сохранять и оно существует
    if (Boolean.FALSE.equals(operation.getSaveImage()) && currentImageUrl != null) {
      try {
        fileStorageRepository.deleteMediaByLink(currentImageUrl);
        category.setImageUrl(null);
        return UpdateCategoryById.Result.success(category.getSlug());
      } catch (Exception e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return UpdateCategoryById.Result.deleteMediaError(e);
      }
    }

    // Загрузка нового изображения
    if (operation.getImageFile() != null && !operation.getImageFile().isEmpty()) {
      try {
        String newUrl = fileStorageRepository.uploadImageToFolder(
            operation.getImageFile(),
            FileStorageFolders.CATEGORY_IMAGES.getValue()
        );
        category.setImageUrl(new CategoryImageUrl(newUrl));
      } catch (Exception e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return UpdateCategoryById.Result.uploadImageError(e);
      }

      // Удаление старого изображения после успешной загрузки нового
      if (currentImageUrl != null) {
        try {
          fileStorageRepository.deleteMediaByLink(currentImageUrl);
        } catch (Exception e) {
          log.warn("Failed to delete old image: {}", currentImageUrl, e);
          // Не прерываем операцию при ошибке удаления старого файла
        }
      }
    }

    return UpdateCategoryById.Result.success(category.getSlug());
  }

  private UpdateCategoryById.Result processCategoryIcon(Category category,
                                                        UpdateCategoryById operation) {
    String currentIconUrl = category.getIconUrl() != null ?
        StringUtils.trimToNull(category.getIconUrl().toString()) : null;

    // Удаление иконки если не нужно сохранять и она существует
    if (Boolean.FALSE.equals(operation.getSaveIcon()) && currentIconUrl != null) {
      try {
        fileStorageRepository.deleteMediaByLink(currentIconUrl);
        category.setIconUrl(null);
        return UpdateCategoryById.Result.success(category.getSlug());
      } catch (Exception e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return UpdateCategoryById.Result.deleteMediaError(e);
      }
    }

    // Загрузка новой иконки
    if (operation.getIconFile() != null && !operation.getIconFile().isEmpty()) {
      try {
        String newUrl = fileStorageRepository.uploadImageToFolder(
            operation.getIconFile(),
            FileStorageFolders.CATEGORY_ICONS.getValue()
        );
        category.setIconUrl(new CategoryIconUrl(newUrl));
      } catch (Exception e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return UpdateCategoryById.Result.uploadImageError(e);
      }

      // Удаление старой иконки после успешной загрузки новой
      if (currentIconUrl != null) {
        try {
          fileStorageRepository.deleteMediaByLink(currentIconUrl);
        } catch (Exception e) {
          log.warn("Failed to delete old icon: {}", currentIconUrl, e);
          // Не прерываем операцию при ошибке удаления старого файла
        }
      }
    }

    return UpdateCategoryById.Result.success(category.getSlug());
  }

  @Override
  @Transactional
  public DeleteCategoryById.Result deleteCategoryById(DeleteCategoryById operation) {
    Optional<Category> category = categoryRepository.getById(operation.getId());

    if (category.isEmpty()) {
      return DeleteCategoryById.Result.notFound(operation.getId());
    }

    if (category.get().getImageUrl() != null &&
        !category.get().getImageUrl().toString().isEmpty()) {
      try {
        fileStorageRepository.deleteMediaByLink(category.get().getImageUrl().toString());
      } catch (Exception e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return DeleteCategoryById.Result.deleteMediaError(e);
      }
    }

    try {
      categoryRepository.delete(category.get());

      try {
        categoryCacheManager.clear();
        categoryListCacheManager.clearAll();
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }

      generalCacheService.clear();
      return DeleteCategoryById.Result.success(category.get().getSlug());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return DeleteCategoryById.Result.deleteError(e);
    }
  }

  private Map<Long, CategoryDto> createCategoryMap(List<CategoryDto> dtos) {
    Map<Long, CategoryDto> result = new HashMap<>();

    for (CategoryDto dto : dtos) {
      if (!dto.getChildren().isEmpty()) {
        Map<Long, CategoryDto> childMap = createCategoryMap(dto.getChildren());
        result.putAll(childMap);
      }

      result.put(dto.getId(), dto);
    }

    return result;
  }

  /**
   * Результат валидации родительской категории
   */
  private record CategoryLevelInfo(Category parentCategory, int parentCategoryLevel) {
  }

  // Вспомогательный класс для обработки результатов
  private record Result<T>(T data, Exception error, boolean success) {

    public static <T> Result<T> success(T data) {
      return new Result<>(data, null, true);
    }

    public static <T> Result<T> error(Exception error) {
      return new Result<>(null, error, false);
    }

    public static <T> Result<T> error(String message) {
      return new Result<>(null, new RuntimeException(message), false);
    }
  }
}