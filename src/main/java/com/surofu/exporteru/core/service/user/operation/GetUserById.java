package com.surofu.exporteru.core.service.user.operation;

import com.surofu.exporteru.application.dto.AbstractAccountDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetUserById {
    Long userId;
    Locale locale;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(AbstractAccountDto dto) {
            log.info("Successfully processed user by ID: {}", dto.getId());
            return Success.of(dto);
        }

        static Result notFound(Long id) {
            log.warn("User with ID '{}' not found", id);
            return NotFound.of(id);
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
            Long userId;

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
