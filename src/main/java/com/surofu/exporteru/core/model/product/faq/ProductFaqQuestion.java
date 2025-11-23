package com.surofu.exporteru.core.model.product.faq;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductFaqQuestion implements Serializable {

    @Column(name = "question", nullable = false, columnDefinition = "text")
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "question_translations")
    private Map<String, String> translations = new HashMap<>();

    private ProductFaqQuestion(String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Вопрос не может быть пустым");
        }

        if (question.length() >= 20_000) {
            throw new IllegalArgumentException("Вопрос не может быть больше 20,000 символов");
        }

        this.value = question;
    }

    public static ProductFaqQuestion of(String question) {
        return new ProductFaqQuestion(question);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductFaqQuestion productFaqQuestion)) return false;
        return Objects.equals(value, productFaqQuestion.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
