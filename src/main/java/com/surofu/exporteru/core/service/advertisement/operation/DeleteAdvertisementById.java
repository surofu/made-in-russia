package com.surofu.exporteru.core.service.advertisement.operation;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteAdvertisementById {
    Long advertisementId;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Long advertisementId) {
            log.info("Successfully processed delete advertisement by ID '{}'", advertisementId);
            return Success.INSTANCE;
        }

        static Result notFound(Long advertisementId) {
            log.warn("Advertisement with ID '{}' not found", advertisementId);
            return NotFound.of(advertisementId);
        }

        static Result deletingFileError(Exception e) {
            log.error("Error while deleting file: {}", e.getMessage());
            return DeletingFileError.INSTANCE;
        }

        static Result deletingAdvertisementError(Exception e) {
            log.error("Error while deleting advertisement: {}", e.getMessage());
            return DeletingAdvertisementError.INSTANCE;
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

        enum DeletingFileError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processDeletingFileError(this);
            }
        }

        enum DeletingAdvertisementError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processDeletingAdvertisementError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processNotFound(NotFound result);
            T processDeletingFileError(DeletingFileError result);
            T processDeletingAdvertisementError(DeletingAdvertisementError result);
        }
    }
}
