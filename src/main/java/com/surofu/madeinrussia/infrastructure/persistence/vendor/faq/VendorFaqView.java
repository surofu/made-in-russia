package com.surofu.madeinrussia.infrastructure.persistence.vendor.faq;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaqAnswer;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaqCreationDate;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaqLastModificationDate;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaqQuestion;

public interface VendorFaqView {
    Long getId();

    VendorFaqQuestion getQuestion();

    VendorFaqAnswer getAnswer();

    VendorFaqCreationDate getCreationDate();

    VendorFaqLastModificationDate getLastModificationDate();
}
