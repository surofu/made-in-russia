package com.surofu.madeinrussia.core.model.product.productMedia;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductMediaAltText implements Serializable {

    @Column(name = "alt_text", nullable = false)
    private String value;

    private ProductMediaAltText(String altText) {
        if (altText == null || altText.trim().isEmpty()) {
            throw new IllegalArgumentException("Альтернативное описание медиа товара не может быть пустым");
        }

        if (altText.length() > 255) {
            throw new IllegalArgumentException("Альтернативное описание медиа товара не может быть больше 255 символов");
        }

        this.value = altText;
    }

    public static ProductMediaAltText of(String altText) {
        return new ProductMediaAltText(altText);
    }

    @Override
    public String toString() {
        return value;
    }
}
