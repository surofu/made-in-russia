package com.surofu.exporteru.infrastructure.persistence.vendor.faq;

import java.time.Instant;

public interface VendorFaqView {
    Long getId();

    String getQuestion();

    String getAnswer();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
