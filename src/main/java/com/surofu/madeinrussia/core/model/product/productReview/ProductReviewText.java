package com.surofu.madeinrussia.core.model.product.productReview;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductReviewText implements Serializable {

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    private ProductReviewText(String text) {
        this.text = text;
    }

    public static ProductReviewText of(String text) {
        return new ProductReviewText(text);
    }

    @Override
    public String toString() {
        return text;
    }
}
