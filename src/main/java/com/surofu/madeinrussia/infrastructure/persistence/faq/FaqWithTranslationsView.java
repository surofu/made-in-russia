package com.surofu.madeinrussia.infrastructure.persistence.faq;

import java.time.Instant;

public interface FaqWithTranslationsView {
    Long getId();

    String getQuestion();

    String getQuestionTranslations();

    String getAnswer();

    String getAnswerTranslations();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
