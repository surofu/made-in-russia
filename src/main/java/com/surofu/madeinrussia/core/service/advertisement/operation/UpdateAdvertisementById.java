package com.surofu.madeinrussia.core.service.advertisement.operation;

import com.surofu.madeinrussia.core.model.advertisement.*;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Value(staticConstructor = "of")
public class UpdateAdvertisementById {
    Long advertisementId;
    AdvertisementTitle title;
    AdvertisementSubtitle subtitle;
    AdvertisementThirdText thirdText;
    AdvertisementLink link;
    AdvertisementIsBig isBig;
    AdvertisementExpirationDate expirationDate;
    MultipartFile image;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Long advertisementId) {
            log.info("Successfully processed update advertisement by id: {}", advertisementId);
            return Success.INSTANCE;
        }

        static Result notFound(Long advertisementId) {
            log.warn("Advertisement with ID '{}' not found.", advertisementId);
            return NotFound.of(advertisementId);
        }

        static Result translationError(Exception e) {
            log.error("Translation error while updating advertisement: {}", e.getMessage());
            return TranslationError.INSTANCE;
        }

        static Result savingFileError(Exception e) {
            log.error("Saving file error while updating advertisement: {}", e.getMessage());
            return SavingFileError.INSTANCE;
        }

        static Result emptyTranslation(Exception e) {
            log.warn("Empty translation error while updating advertisement: {}", e.getMessage());
            return EmptyTransaction.INSTANCE;
        }

        static Result savingAdvertisementError(Exception e) {
            log.error("Saving advertisement error while updating advertisement: {}", e.getMessage());
            return SavingAdvertisementError.INSTANCE;
        }

        static Result emptyFile() {
            log.warn("Empty file while updating advertisement");
            return EmptyTransaction.INSTANCE;
        }

        static Result deletingFileError(Exception e) {
            log.error("Deleting file error while updating advertisement: {}", e.getMessage());
            return DeletingFileError.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            Long advertisementId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processNotFound(this);
            }
        }

        enum TranslationError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processTranslationError(this);
            }
        }

        enum SavingFileError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSavingFileError(this);
            }
        }

        enum EmptyTransaction implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processEmptyTransaction(this);
            }
        }

        enum SavingAdvertisementError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSavingAdvertisementError(this);
            }
        }

        enum EmptyFile implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processEmptyFile(this);
            }
        }

        enum DeletingFileError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processDeletingFileError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processNotFound(NotFound result);
            T processTranslationError(TranslationError result);
            T processSavingFileError(SavingFileError result);
            T processEmptyTransaction(EmptyTransaction result);
            T processSavingAdvertisementError(SavingAdvertisementError result);
            T processEmptyFile(EmptyFile result);
            T processDeletingFileError(DeletingFileError result);
        }
    }
}
