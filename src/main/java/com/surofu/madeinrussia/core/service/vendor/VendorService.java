package com.surofu.madeinrussia.core.service.vendor;

import com.surofu.madeinrussia.core.service.vendor.operation.CreateVendorFaq;
import com.surofu.madeinrussia.core.service.vendor.operation.GetVendorById;
import com.surofu.madeinrussia.core.service.vendor.operation.GetVendorReviewPageById;

public interface VendorService {
    GetVendorById.Result getVendorById(GetVendorById operation);

    GetVendorReviewPageById.Result getVendorReviewPageById(GetVendorReviewPageById operation);

    CreateVendorFaq.Result createVendorFaq(CreateVendorFaq operation);
}
