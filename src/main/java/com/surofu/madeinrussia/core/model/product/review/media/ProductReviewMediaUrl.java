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
public final class ProductReviewMediaUrl implements Serializable {

    @Column(name = "url", nullable = false, columnDefinition = "text")
    private String value;

    private ProductReviewMediaUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Ссылка медиа отзыва не может быть пустой");
        }

        if (url.length() >= 20_000) {
            throw new IllegalArgumentException("Ссылка медиа отзыва не может быть больше 20,000 символов");
        }

        this.value = url;
    }

    public static ProductReviewMediaUrl of(String url) {
        return new ProductReviewMediaUrl(url);
    }

    @Override
    public String toString() {
        return value;
    }
}
