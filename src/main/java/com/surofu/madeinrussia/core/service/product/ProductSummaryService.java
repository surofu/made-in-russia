package com.surofu.madeinrussia.core.service.product;

import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewById;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewPage;

public interface ProductSummaryService {
    GetProductSummaryViewPage.Result getProductSummaryPage(GetProductSummaryViewPage operation);

    GetProductSummaryViewById.Result getProductSummaryViewById(GetProductSummaryViewById operation);
}
