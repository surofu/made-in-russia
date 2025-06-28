package com.surofu.madeinrussia.core.service.product;

import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewById;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewPage;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewPageByVendorId;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewsByIds;

public interface ProductSummaryService {
    GetProductSummaryViewPage.Result getProductSummaryPage(GetProductSummaryViewPage operation);

    GetProductSummaryViewById.Result getProductSummaryViewById(GetProductSummaryViewById operation);

    GetProductSummaryViewPageByVendorId.Result getProductSummaryViewPageByVendorId(GetProductSummaryViewPageByVendorId operation);

    GetProductSummaryViewsByIds.Result getProductSummaryViewsByIds(GetProductSummaryViewsByIds operation);
}
