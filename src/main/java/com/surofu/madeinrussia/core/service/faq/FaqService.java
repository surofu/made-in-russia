package com.surofu.madeinrussia.core.service.faq;

import com.surofu.madeinrussia.core.service.faq.operation.*;

public interface FaqService {
    GetAllFaq.Result getAllFaq(GetAllFaq operation);

    GetFaqById.Result getFaqById(GetFaqById operation);

    GetFaqWithTranslationsById.Result getFaqWithTranslationsById(GetFaqWithTranslationsById operation);

    CreateFaq.Result createFaq(CreateFaq operation);

    UpdateFaqById.Result updateFaqById(UpdateFaqById operation);

    DeleteFaqById.Result deleteFaqById(DeleteFaqById operation);
}
