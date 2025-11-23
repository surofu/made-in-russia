package com.surofu.exporteru.infrastructure.persistence.faq;

import java.time.Instant;
import java.util.Map;

public interface FaqWithTranslationsView {
    Long getId();

    String getQuestion();

    Map<String, String> getQuestionTranslations();

    String getAnswer();

    Map<String, String> getAnswerTranslations();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
