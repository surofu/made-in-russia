package com.surofu.exporteru.core.model.product.faq;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
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
public final class ProductFaqAnswer implements Serializable {

    @Column(name = "answer", nullable = false, columnDefinition = "text")
    private final String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answer_translations")
    private final Map<String, String> translations;

    public ProductFaqAnswer(String answer, Map<String, String> translations) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.faq.answer.empty");
        }

        if (answer.length() >= 20_000) {
            throw new LocalizedValidationException("validation.faq.answer.max_length");
        }

        this.value = answer;
        this.translations = translations;
    }

    public ProductFaqAnswer() {
        this.value = null;
        this.translations = new HashMap<>();
    }

    @Override
    public String toString() {
        return value;
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
        if (!(o instanceof ProductFaqAnswer that)) {
            return false;
        }
      return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
