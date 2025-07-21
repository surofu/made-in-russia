package com.surofu.madeinrussia.infrastructure.persistence.vendor.faq;

import java.time.Instant;

public interface VendorFaqWithTranslationsView {
    Long getId();

    String getQuestion();

    String getQuestionTranslations();

    String getAnswer();

    String getAnswerTranslations();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
