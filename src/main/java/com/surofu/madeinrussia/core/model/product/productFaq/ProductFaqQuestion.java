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

    @Column(name = "question", columnDefinition = "text", nullable = false)
    private String question;

    private ProductFaqQuestion(String question) {
        this.question = question;
    }

    public static ProductFaqQuestion of(String question) {
        return new ProductFaqQuestion(question);
    }

    @Override
    public String toString() {
        return question;
    }
}
