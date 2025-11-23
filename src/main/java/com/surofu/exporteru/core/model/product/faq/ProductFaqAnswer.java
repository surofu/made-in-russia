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
public final class ProductFaqAnswer implements Serializable {

    @Column(name = "answer", nullable = false, columnDefinition = "text")
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answer_translations")
    private Map<String, String> translations = new HashMap<>();

    private ProductFaqAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Ответ не может быть пустым");
        }

        if (answer.length() >= 20_000) {
            throw new IllegalArgumentException("Ответ не может быть больше 20,000 символов");
        }

        this.value = answer;
    }

    public static ProductFaqAnswer of(String answer) {
        return new ProductFaqAnswer(answer);
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
        return Objects.hash(value);
    }
}
