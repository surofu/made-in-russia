package com.surofu.exporteru.core.service.seo.operation;

import com.surofu.exporteru.application.dto.seo.SeoDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetSeo {

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(SeoDto seo) {
            log.info("Successfully processed seo: Products: {}; Vendors: {}", seo.getProducts().size(), seo.getVendors().size());
            return Success.of(seo);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            SeoDto seo;

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
