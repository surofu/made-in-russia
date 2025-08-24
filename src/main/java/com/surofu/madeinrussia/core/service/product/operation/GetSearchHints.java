package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.SearchHintDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class GetSearchHints {
    String searchTerm;
    Long vendorId;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(List<SearchHintDto> searchHints) {
            log.info("Successfully processed get search hints: {}", searchHints.size());
            return Success.of(searchHints);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            List<SearchHintDto> searchHints;

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
