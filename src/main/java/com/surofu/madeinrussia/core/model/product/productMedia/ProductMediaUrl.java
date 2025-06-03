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
public final class ProductMediaUrl implements Serializable {

    @Column(name = "url", nullable = false, columnDefinition = "text")
    private String value;

    private ProductMediaUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Ссылка медиа товара не может быть пустой");
        }

        if (url.length() > 20000) {
            throw new IllegalArgumentException("Ссылка медиа товара не может быть больше 20,000 символов");
        }

        this.value = url;
    }

    public static ProductMediaUrl of(String url) {
        return new ProductMediaUrl(url);
    }

    @Override
    public String toString() {
        return value;
    }
}
