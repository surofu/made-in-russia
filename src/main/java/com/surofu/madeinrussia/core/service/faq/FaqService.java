package com.surofu.madeinrussia.core.service.faq;

import com.surofu.madeinrussia.core.service.faq.operation.CreateFaq;
import com.surofu.madeinrussia.core.service.faq.operation.DeleteFaqById;
import com.surofu.madeinrussia.core.service.faq.operation.GetAllFaq;
import com.surofu.madeinrussia.core.service.faq.operation.UpdateFaqById;

public interface FaqService {
    GetAllFaq.Result getAllFaq(GetAllFaq operation);

    CreateFaq.Result createFaq(CreateFaq operation);

    UpdateFaqById.Result updateFaqById(UpdateFaqById operation);

    DeleteFaqById.Result deleteFaqById(DeleteFaqById operation);
}
