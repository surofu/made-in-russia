package com.surofu.madeinrussia.core.service.user.operation;

import com.surofu.madeinrussia.application.dto.AbstractAccountDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

@Slf4j
@Value(staticConstructor = "of")
public class GetUserPage {
    Integer page;
    Integer size;
    String role;
    Boolean isEnabled;
    String login;
    String email;
    String phoneNumber;
    String region;
    String sort;
    String direction;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Page<AbstractAccountDto> page) {
            log.info("Successfully processed get user page: {}", page.getTotalElements());
            return Success.of(page);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            Page<AbstractAccountDto> page;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
        }
    }
}
