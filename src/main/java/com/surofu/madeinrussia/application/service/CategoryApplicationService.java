package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.cache.CategoryCacheManager;
import com.surofu.madeinrussia.application.cache.CategoryListCacheManager;
import com.surofu.madeinrussia.application.cache.GeneralCacheService;
import com.surofu.madeinrussia.application.dto.category.CategoryDto;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.enums.FileStorageFolders;
import com.surofu.madeinrussia.application.exception.EmptyTranslationException;
import com.surofu.madeinrussia.application.utils.CategoryUtils;
import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.category.CategoryImageUrl;
import com.surofu.madeinrussia.core.model.category.CategorySlug;
import com.surofu.madeinrussia.core.model.okved.OkvedCategory;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import com.surofu.madeinrussia.core.repository.FileStorageRepository;
import com.surofu.madeinrussia.core.repository.TranslationRepository;
import com.surofu.madeinrussia.core.service.category.CategoryService;
import com.surofu.madeinrussia.core.service.category.operation.*;
import com.surofu.madeinrussia.infrastructure.persistence.category.CategoryView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

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
        List<CategoryDto> cachedCategoryDtos = categoryListCacheManager.getAllByLocale("ALL_" + operation.getLocale().getLanguage());
        if (cachedCategoryDtos != null) {
            log.info("Found {} cached Categories", cachedCategoryDtos.size());
            return GetAllCategories.Result.success(cachedCategoryDtos);
        }

        // Process
        List<CategoryView> categories = categoryRepository.getAllCategoriesViewsByLang(operation.getLocale().getLanguage());
        List<CategoryDto> categoryDtos = CategoryUtils.buildTreeFromViews(categories);

        try {
            categoryListCacheManager.setAllByLocale("ALL_" + operation.getLocale().getLanguage(), categoryDtos);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return GetAllCategories.Result.success(categoryDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCategories.Result getCategories(GetCategories operation) {
        // Check cache
        List<CategoryDto> cachedCategoryDtos = categoryListCacheManager.getAllByLocale(operation.getLocale().getLanguage());
        if (cachedCategoryDtos != null) {
            return GetCategories.Result.success(cachedCategoryDtos);
        }

        // Process
        List<CategoryView> categories = categoryRepository.getCategoryViewsL1AndL2ByLang(operation.getLocale().getLanguage());
        List<CategoryDto> categoryDtos = CategoryUtils.buildTreeFromViews(categories);

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
        CategoryDto cachedCategoryDto = categoryCacheManager.getCategory(operation.getCategoryId(), operation.getLocale());

        if (cachedCategoryDto != null) {
            return GetCategoryById.Result.success(cachedCategoryDto);
        }

        // Process
        List<CategoryView> categories = categoryRepository.getCategoryViewWithChildrenByIdAndLang(operation.getCategoryId(), operation.getLocale().getLanguage());
        List<CategoryDto> categoryDtos = CategoryUtils.buildTreeFromViews(categories);

        if (categoryDtos.isEmpty()) {
            return GetCategoryById.Result.notFound(operation.getCategoryId());
        }

        CategoryDto categoryDto = categoryDtos.stream().findFirst().get();

        try {
            categoryCacheManager.setCategory(operation.getCategoryId(), operation.getLocale(), categoryDto);
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
        CategoryDto cachedCategoryDto = categoryCacheManager.getCategory(operation.getCategorySlug().toString(), operation.getLocale());

        if (cachedCategoryDto != null) {
            return GetCategoryBySlug.Result.success(cachedCategoryDto);
        }

        // Process
        List<CategoryView> categories = categoryRepository.getCategoryViewWithChildrenBySlugAndLang(operation.getCategorySlug().toString(), operation.getLocale().getLanguage());
        List<CategoryDto> categoryDtos = CategoryUtils.buildTreeFromViews(categories);

        if (categoryDtos.isEmpty()) {
            return GetCategoryBySlug.Result.notFound(operation.getCategorySlug());
        }

        CategoryDto categoryDto = categoryDtos.stream().findFirst().get();

        try {
            categoryCacheManager.setCategory(operation.getCategorySlug().toString(), operation.getLocale(), categoryDto);
            categoryCacheManager.setCategory(categoryDto.getId().toString(), operation.getLocale(), categoryDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return GetCategoryBySlug.Result.success(categoryDto);
    }

    @Override
    @Transactional
    public CreateCategory.Result createCategory(CreateCategory operation) {
        HstoreTranslationDto translationResult;

        try {
            translationResult = translationRepository.expand(HstoreTranslationDto.ofNullable(operation.getNameTranslations()));
        } catch (EmptyTranslationException e) {
            return CreateCategory.Result.emptyTranslations();
        } catch (Exception e) {
            return CreateCategory.Result.translationError(e);
        }

        Optional<Category> parentCategory = Optional.empty();
        int parentCategoryLevel = 1;

        if (operation.getParentId() != null) {
            parentCategory = categoryRepository.getCategoryById(operation.getParentId());

            if (parentCategory.isEmpty()) {
                return CreateCategory.Result.parentNotFound(operation.getParentId());
            }

            try {
                parentCategoryLevel = 1 + Integer.parseInt(parentCategory.get().getSlug().getValue().split("_")[0].split("l")[1]);
            } catch (NumberFormatException e) {
                return CreateCategory.Result.parentSlugLevelParseError(parentCategory.get().getSlug(), e);
            }
        }

        CategorySlug slugWithLevel = CategorySlug.of(operation.getSlug().toString(), parentCategoryLevel);

        if (categoryRepository.existsBySlug(slugWithLevel)) {
            return CreateCategory.Result.slugAlreadyExists(slugWithLevel);
        }

        Category category = new Category();
        category.setParent(parentCategory.orElse(null));
        category.setName(operation.getName());
        category.setSlug(slugWithLevel);

        Set<OkvedCategory> okvedCategorySet = new HashSet<>();

        for (String okvedId : operation.getOkvedCategories()) {
            OkvedCategory okvedCategory = new OkvedCategory();
            okvedCategory.setCategory(category);
            okvedCategory.setOkvedId(okvedId);
            okvedCategorySet.add(okvedCategory);
        }

        category.setOkvedCategories(okvedCategorySet);

        category.getName().setTranslations(translationResult);

        if (operation.getImageFile() != null && !operation.getImageFile().isEmpty()) {
            try {
                String url = fileStorageRepository.uploadImageToFolder(operation.getImageFile(), FileStorageFolders.CATEGORY_IMAGES.getValue());
                category.setImageUrl(CategoryImageUrl.of(url));
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return CreateCategory.Result.uploadImageError(e);
            }
        }

        try {
            categoryRepository.save(category);

            try {
                categoryListCacheManager.clearAll();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            generalCacheService.clear();

            return CreateCategory.Result.success(category.getSlug());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateCategory.Result.saveError(category.getSlug(), e);
        }
    }

    @Override
    @Transactional
    public UpdateCategoryById.Result updateCategoryById(UpdateCategoryById operation) {
        Optional<Category> category = categoryRepository.getCategoryById(operation.getId());

        if (category.isEmpty()) {
            return UpdateCategoryById.Result.notFound(operation.getId());
        }

        HstoreTranslationDto translationResult;

        try {
            translationResult = translationRepository.expand(HstoreTranslationDto.ofNullable(operation.getNameTranslations()));
        } catch (EmptyTranslationException e) {
            return UpdateCategoryById.Result.emptyTranslations();
        } catch (Exception e) {
            return UpdateCategoryById.Result.translationError(e);
        }

        Optional<Category> parentCategory = Optional.empty();
        int parentCategoryLevel = 1;

        if (operation.getParentId() != null) {
            parentCategory = categoryRepository.getCategoryById(operation.getParentId());

            if (parentCategory.isEmpty()) {
                return UpdateCategoryById.Result.parentNotFound(operation.getParentId());
            }

            try {
                parentCategoryLevel = 1 + Integer.parseInt(parentCategory.get().getSlug().getValue().split("_")[0].split("l")[1]);
            } catch (NumberFormatException e) {
                return UpdateCategoryById.Result.parentSlugLevelParseError(parentCategory.get().getSlug(), e);
            }
        }

        CategorySlug slugWithLevel = CategorySlug.of(operation.getSlug().toString(), parentCategoryLevel);

        if (!category.get().getSlug().getValue().equals(slugWithLevel.getValue()) && categoryRepository.existsBySlug(slugWithLevel)) {
            return UpdateCategoryById.Result.slugAlreadyExists(slugWithLevel);
        }

        category.get().setParent(parentCategory.orElse(null));
        category.get().setName(operation.getName());
        category.get().getName().setTranslations(translationResult);
        category.get().setSlug(slugWithLevel);

        Iterator<OkvedCategory> okvedCategoryIterator = category.get().getOkvedCategories().iterator();
        for (int i = 0; i < operation.getOkvedCategories().size(); i++) {
            String okvedId = operation.getOkvedCategories().get(i);

            if (okvedCategoryIterator.hasNext()) {
                OkvedCategory okvedCategory = okvedCategoryIterator.next();
                okvedCategory.setCategory(category.get());
                okvedCategory.setOkvedId(okvedId);
            } else {
                OkvedCategory okvedCategory = new OkvedCategory();
                okvedCategory.setCategory(category.get());
                okvedCategory.setOkvedId(okvedId);
                category.get().getOkvedCategories().add(okvedCategory);
            }
        }

        boolean hasNewImage = false;
        if (operation.getImageFile() != null && !operation.getImageFile().isEmpty()) {
            try {
                String url = fileStorageRepository.uploadImageToFolder(operation.getImageFile(), FileStorageFolders.CATEGORY_IMAGES.getValue());
                category.get().setImageUrl(CategoryImageUrl.of(url));
                hasNewImage = true;
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return UpdateCategoryById.Result.uploadImageError(e);
            }
        }

        if (hasNewImage && !operation.getSaveImage()) {
            try {
                fileStorageRepository.deleteMediaByLink(category.get().getImageUrl().toString());
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return UpdateCategoryById.Result.deleteMediaError(e);
            }
        }

        try {
            categoryRepository.save(category.get());
            try {
                categoryCacheManager.clear();
                categoryListCacheManager.clearAll();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            generalCacheService.clear();

            return UpdateCategoryById.Result.success(category.get().getSlug());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateCategoryById.Result.saveError(category.get().getSlug(), e);
        }
    }

    @Override
    @Transactional
    public DeleteCategoryById.Result deleteCategoryById(DeleteCategoryById operation) {
        Optional<Category> category = categoryRepository.getCategoryById(operation.getId());

        if (category.isEmpty()) {
            return DeleteCategoryById.Result.notFound(operation.getId());
        }

        if (category.get().getImageUrl() != null && !category.get().getImageUrl().toString().isEmpty()) {
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
}