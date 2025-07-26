package com.surofu.madeinrussia.infrastructure.persistence.faq;

import java.time.Instant;

public interface FaqView {
    Long getId();

    String getQuestion();

    String getAnswer();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
