package com.surofu.exporteru.core.model.product.faq;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;

import java.io.Serializable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
public final class ProductFaqQuestion implements Serializable {

    @Column(name = "question", nullable = false, columnDefinition = "text")
    private final String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "question_translations")
    private final Map<String, String> translations;

    public ProductFaqQuestion(String question, Map<String, String> translations) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Вопрос не может быть пустым");
        }

        if (question.length() >= 20_000) {
            throw new IllegalArgumentException("Вопрос не может быть больше 20,000 символов");
        }

        this.value = question;
        this.translations = translations;
    }

    public ProductFaqQuestion() {
        this("Question", new HashMap<>());
    }

    public String getLocalizedValue() {
        if (translations == null || translations.isEmpty()) {
            return Objects.requireNonNullElse(value, "");
        }

        Locale locale = LocaleContextHolder.getLocale();
        return translations.getOrDefault(locale.getLanguage(), Objects.requireNonNullElse(value, ""));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductFaqQuestion productFaqQuestion)) return false;
        return Objects.equals(value, productFaqQuestion.value);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
