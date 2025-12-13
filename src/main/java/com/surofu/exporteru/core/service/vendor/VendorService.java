package com.surofu.exporteru.core.service.vendor;

import com.surofu.exporteru.core.service.vendor.operation.*;

public interface VendorService {
    GetVendorById.Result getVendorById(GetVendorById operation);

    GetVendorReviewPageById.Result getVendorReviewPageById(GetVendorReviewPageById operation);

    CreateVendorFaq.Result createVendorFaq(CreateVendorFaq operation);

    UpdateVendorFaq.Result updateVendorFaq(UpdateVendorFaq operation);

    DeleteVendorFaqById.Result deleteVendorFaqById(DeleteVendorFaqById operation);

    ForceUpdateVendorById.Result forceUpdateVendorById(ForceUpdateVendorById operation);

    SendCallRequestMail.Result sendCallRequestMail(SendCallRequestMail operation);
}
