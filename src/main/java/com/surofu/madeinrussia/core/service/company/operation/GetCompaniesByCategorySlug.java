package com.surofu.madeinrussia.core.service.company.operation;

import com.surofu.madeinrussia.core.model.category.CategorySlug;
import com.surofu.madeinrussia.core.model.okved.OkvedCompany;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class GetCompaniesByCategorySlug {
    CategorySlug categorySlug;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(List<OkvedCompany> okvedCompanyList) {
            log.info("Successfully processed get okved companies by category id: {}", okvedCompanyList.size());
            return Success.of(okvedCompanyList);
        }

        static Result notFound(CategorySlug categorySlug) {
            log.warn("Category with slug '{}' not found", categorySlug.toString());
            return NotFound.of(categorySlug);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            List<OkvedCompany> okvedCompanyList;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            CategorySlug categorySlug;

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
