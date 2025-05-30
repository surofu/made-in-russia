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

    @Column(name = "answer", columnDefinition = "text", nullable = false)
    private String value;

    private ProductFaqAnswer(String answer) {
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
