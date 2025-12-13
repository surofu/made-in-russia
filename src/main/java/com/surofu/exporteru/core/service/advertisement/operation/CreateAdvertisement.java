package com.surofu.exporteru.core.service.advertisement.operation;

import com.surofu.exporteru.core.model.advertisement.*;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Value(staticConstructor = "of")
public class CreateAdvertisement {
    AdvertisementTitle title;
    AdvertisementSubtitle subtitle;
    AdvertisementThirdText thirdText;
    AdvertisementLink link;
    AdvertisementIsBig isBig;
    AdvertisementExpirationDate expirationDate;
    MultipartFile image;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success() {
            log.info("Successfully processed save advertisement");
            return Success.INSTANCE;
        }

        static Result translationError(Exception e) {
            log.error("Translation error while saving advertisement: {}", e.getMessage());
            return TranslationError.INSTANCE;
        }

        static Result savingFileError(Exception e) {
            log.error("Saving file error while saving advertisement: {}", e.getMessage());
            return SavingFileError.INSTANCE;
        }

        static Result emptyTranslation(Exception e) {
            log.warn("Empty translation error while saving advertisement: {}", e.getMessage());
            return EmptyTransaction.INSTANCE;
        }

        static Result savingAdvertisementError(Exception e) {
            log.error("Saving advertisement error while saving advertisement: {}", e.getMessage());
            return SavingAdvertisementError.INSTANCE;
        }

        static Result emptyFile() {
            log.warn("Empty file while saving advertisement");
            return EmptyTransaction.INSTANCE;
        }

        static Result deletingFileError(Exception e) {
            log.error("Deleting file error while saving advertisement: {}", e.getMessage());
            return DeletingFileError.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
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
            T processTranslationError(TranslationError result);
            T processSavingFileError(SavingFileError result);
            T processEmptyTransaction(EmptyTransaction result);
            T processSavingAdvertisementError(SavingAdvertisementError result);
            T processEmptyFile(EmptyFile result);
            T processDeletingFileError(DeletingFileError result);
        }
    }
}
