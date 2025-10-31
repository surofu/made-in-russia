package com.surofu.exporteru.core.service.me.operation;

import com.surofu.exporteru.application.dto.AbstractAccountDto;
import com.surofu.exporteru.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteMeVendorMediaById {
    SecurityUser securityUser;
    Long id;
    Locale locale;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(AbstractAccountDto dto, Long vendorMediaId) {
            log.info("Successfully deleted vendor media with ID '{}'", vendorMediaId);
            return Success.of(dto);
        }

        static Result notFound(Long vendorMediaId) {
            log.warn("Vendor media not found with ID '{}'", vendorMediaId);
            return NotFound.of(vendorMediaId);
        }

        static Result deleteMediaError(Exception e, Long vendorMediaId) {
            log.error("Error while deleting vendor media with ID '{}'", vendorMediaId, e);
            return DeleteMediaError.INSTANCE;
        }

        static Result saveError(Exception e, Long vendorMediaId) {
            log.error("Error while storing vendor media with ID '{}'", vendorMediaId, e);
            return SaveError.INSTANCE;
        }

        static Result invalidPosition(Long vendorMediaId) {
            log.warn("Invalid media position while deleve me vendor media with ID '{}'", vendorMediaId);
            return InvalidPosition.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            AbstractAccountDto dto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            Long id;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processNotFound(this);
            }
        }

        enum DeleteMediaError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processDeleteMediaError(this);
            }
        }

        enum SaveError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveError(this);
            }
        }

        enum InvalidPosition implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processInvalidPosition(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processNotFound(NotFound result);

            T processDeleteMediaError(DeleteMediaError result);

            T processSaveError(SaveError result);

            T processInvalidPosition(InvalidPosition result);
        }
    }
}
