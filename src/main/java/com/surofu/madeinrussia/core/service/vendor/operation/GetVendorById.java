package com.surofu.madeinrussia.core.service.vendor.operation;

import com.surofu.madeinrussia.application.dto.AbstractAccountDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Value(staticConstructor = "of")
public class GetVendorById {
    Optional<SecurityUser> securityUser;
    Long vendorId;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(AbstractAccountDto abstractAccountDto) {
            log.info("Successfully processed vendor by ID: {}", abstractAccountDto.getId());
            return Success.of(abstractAccountDto);
        }

        static Result notFound(Long vendorId) {
            log.warn("Vendor with ID '{}' not found", vendorId);
            return NotFound.of(vendorId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            AbstractAccountDto abstractAccountDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            Long vendorId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processNotFound(NotFound result);
        }
    }
}
