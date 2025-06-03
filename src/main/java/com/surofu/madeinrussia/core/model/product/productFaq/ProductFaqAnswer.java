package com.surofu.madeinrussia.core.model.product.productFaq;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductFaqAnswer implements Serializable {

    @Column(name = "answer", nullable = false, columnDefinition = "text")
    private String value;

    private ProductFaqAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Ответ не может быть пустым");
        }

        if (answer.length() > 20000) {
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
}
