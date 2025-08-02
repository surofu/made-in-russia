package com.surofu.madeinrussia.core.service.vendor;

import com.surofu.madeinrussia.core.service.vendor.operation.*;

public interface VendorService {
    GetVendorById.Result getVendorById(GetVendorById operation);

    GetVendorReviewPageById.Result getVendorReviewPageById(GetVendorReviewPageById operation);

    CreateVendorFaq.Result createVendorFaq(CreateVendorFaq operation);

    DeleteVendorFaqById.Result deleteVendorFaqById(DeleteVendorFaqById operation);

    ForceUpdateVendorById.Result forceUpdateVendorById(ForceUpdateVendorById operation);
}
