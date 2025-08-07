package com.surofu.madeinrussia.core.model.product.review.media;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductReviewMediaAltText implements Serializable {

    @Column(name = "alt_text", nullable = false)
    private String value;

    private ProductReviewMediaAltText(String altText) {
        if (altText == null || altText.trim().isEmpty()) {
            throw new IllegalArgumentException("Альтернативное описание медиа отзыва не может быть пустым");
        }

        if (altText.length() > 255) {
            throw new IllegalArgumentException("Альтернативное описание медиа отзыва не может быть больше 255 символов");
        }

        this.value = altText;
    }

    public static ProductReviewMediaAltText of(String altText) {
        return new ProductReviewMediaAltText(altText);
    }

    @Override
    public String toString() {
        return value;
    }
}
