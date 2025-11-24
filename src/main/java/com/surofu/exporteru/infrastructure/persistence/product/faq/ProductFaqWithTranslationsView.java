package com.surofu.exporteru.infrastructure.persistence.product.faq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;

public interface ProductFaqWithTranslationsView {
    Long getId();

    String getQuestion();

    String getQuestionTranslations();

    String getAnswer();

    String getAnswerTranslations();

    Instant getCreationDate();

    Instant getLastModificationDate();

    default Map<String, String> getQuestionTranslationsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(getQuestionTranslations(), new TypeReference<>() {
            });
        } catch (Exception e) {
            return Map.of();
        }
    }

    default Map<String, String> getAnswerTranslationsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(getAnswerTranslations(), new TypeReference<>() {
            });
        } catch (Exception e) {
            return Map.of();
        }
    }
}
