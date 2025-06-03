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
public final class ProductFaqQuestion implements Serializable {

    @Column(name = "question", nullable = false, columnDefinition = "text")
    private String value;

    private ProductFaqQuestion(String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Вопрос не может быть пустым");
        }

        if (question.length() > 20000) {
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
}
