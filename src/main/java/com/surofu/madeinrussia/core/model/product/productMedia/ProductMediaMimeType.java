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
public final class ProductMediaMimeType implements Serializable {

    @Column(name = "mime_type", nullable = false)
    private String value;

    private ProductMediaMimeType(String mimeType) {
        if (mimeType == null || mimeType.trim().isEmpty()) {
            throw new IllegalArgumentException("Тип контента медиа товара не может быть пустым");
        }

        if (mimeType.length() > 255) {
            throw new IllegalArgumentException("Тип контента медиа товара не может быть больше 255 символов");
        }

        this.value = mimeType;
    }

    public static ProductMediaMimeType of(String mimeType) {
        return new ProductMediaMimeType(mimeType);
    }

    @Override
    public String toString() {
        return value;
    }
}
