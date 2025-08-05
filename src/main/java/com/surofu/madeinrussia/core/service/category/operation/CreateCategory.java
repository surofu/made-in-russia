package com.surofu.madeinrussia.core.service.category.operation;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;
import com.surofu.madeinrussia.core.model.category.CategoryName;
import com.surofu.madeinrussia.core.model.category.CategorySlug;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class CreateCategory {
    CategoryName name;
    TranslationDto nameTranslations;
    CategorySlug slug;
    Long parentId;
    List<String> okvedCategories;
    MultipartFile imageFile;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(CategorySlug slug) {
            log.info("Successfully processed create category: {}", slug);
            return Success.INSTANCE;
        }

        static Result slugAlreadyExists(CategorySlug slug) {
            log.warn("Category with slug '{}' already exists", slug);
            return SlugAlreadyExists.of(slug);
        }

        static Result parentNotFound(Long id) {
            log.warn("Parent category with ID '{}' not found", id);
            return ParentNotFound.of(id);
        }

        static Result saveError(CategorySlug slug, Exception e) {
            log.error("Error creating category: {}", slug, e);
            return SaveError.INSTANCE;
        }

        static Result parentSlugLevelParseError(CategorySlug slug, Exception e) {
            log.error("Error parsing parent slug level: {}", slug, e);
            return ParentSlugLevelParseError.INSTANCE;
        }

        static Result emptyTranslations() {
            log.warn("Empty translations found while create category");
            return EmptyTranslations.INSTANCE;
        }

        static Result translationError(Exception e) {
            log.error("Translation error while create category", e);
            return TranslationError.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        static Result uploadImageError(Exception e) {
            log.error("Error uploading image while create category", e);
            return UploadImageError.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class SlugAlreadyExists implements Result {
            CategorySlug slug;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSlugAlreadyExists(this);
            }
        }

        @Value(staticConstructor = "of")
        class ParentNotFound implements Result {
            Long id;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processParentNotFound(this);
            }
        }

        enum SaveError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveError(this);
            }
        }

        enum ParentSlugLevelParseError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processParentSlugLevelParseError(this);
            }
        }

        enum EmptyTranslations implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processEmptyTranslations(this);
            }
        }

        enum TranslationError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processTranslationError(this);
            }
        }

        enum UploadImageError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUploadImageError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processSlugAlreadyExists(SlugAlreadyExists result);
            T processParentNotFound(ParentNotFound result);
            T processSaveError(SaveError result);
            T processParentSlugLevelParseError(ParentSlugLevelParseError result);
            T processEmptyTranslations(EmptyTranslations result);
            T processTranslationError(TranslationError result);
            T processUploadImageError(UploadImageError result);
        }
    }
}
