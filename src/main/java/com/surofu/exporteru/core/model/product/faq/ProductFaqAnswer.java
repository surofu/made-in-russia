package com.surofu.exporteru.core.model.product.faq;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;

import java.io.Serializable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
            throw new IllegalArgumentException("Ответ не может быть пустым");
        }

        if (answer.length() >= 20_000) {
            throw new IllegalArgumentException("Ответ не может быть больше 20,000 символов");
        }

        this.value = answer;
        this.translations = translations;
    }

    public ProductFaqAnswer() {
        this("Answer", new HashMap<>());
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductFaqAnswer productFaqAnswer)) return false;
        return Objects.equals(value, productFaqAnswer.value);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
