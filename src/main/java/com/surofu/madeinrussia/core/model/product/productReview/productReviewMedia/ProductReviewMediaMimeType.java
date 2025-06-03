package com.surofu.madeinrussia.core.model.product.productReview.productReviewMedia;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductReviewMediaMimeType implements Serializable {

    @Column(name = "mime_type", nullable = false)
    private String value;

    private ProductReviewMediaMimeType(String mimeType) {
        if (mimeType == null || mimeType.trim().isEmpty()) {
            throw new IllegalArgumentException("Тип контента медиа отзыва не может быть пустым");
        }

        if (mimeType.length() > 255) {
            throw new IllegalArgumentException("Тип контента медиа отзыва не может быть больше 255 символов");
        }

        this.value = mimeType;
    }

    public static ProductReviewMediaMimeType of(String mimeType) {
        return new ProductReviewMediaMimeType(mimeType);
    }

    @Override
    public String toString() {
        return value;
    }
}
