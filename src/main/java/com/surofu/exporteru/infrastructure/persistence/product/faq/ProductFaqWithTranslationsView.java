package com.surofu.exporteru.infrastructure.persistence.product.faq;

import java.time.Instant;

public interface ProductFaqWithTranslationsView {
    Long getId();

    String getQuestion();

    String getQuestionTranslations();

    String getAnswer();

    String getAnswerTranslations();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
