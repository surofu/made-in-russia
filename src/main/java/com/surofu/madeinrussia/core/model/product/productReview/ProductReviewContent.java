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
public final class ProductReviewContent implements Serializable {

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String value;

    private ProductReviewContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Текст отзыва не может быть пустым");
        }

        if (content.length() >= 10_000) {
            throw new IllegalArgumentException("Текст отзыва не может быть больше 10,000 символов");
        }

        this.value = content;
    }

    public static ProductReviewContent of(String content) {
        return new ProductReviewContent(content);
    }

    @Override
    public String toString() {
        return value;
    }
}
