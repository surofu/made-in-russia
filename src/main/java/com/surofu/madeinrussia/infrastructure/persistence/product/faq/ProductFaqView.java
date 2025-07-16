package com.surofu.madeinrussia.infrastructure.persistence.product.faq;

import java.time.Instant;

public interface ProductFaqView {
    Long getId();

    String getQuestion();

    String getAnswer();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
