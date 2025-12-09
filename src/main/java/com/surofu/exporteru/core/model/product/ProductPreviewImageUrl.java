package com.surofu.exporteru.core.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPreviewImageUrl implements Serializable {

    @Column(name = "preview_image_url", nullable = false, columnDefinition = "text")
    private String value;

    private ProductPreviewImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Ссылка на изображение превью товара не может быть пустым");
        }

        if (url.length() >= 20_000) {
            throw new IllegalArgumentException("Ссылка на изображение превью товара не может быть больше 20,000 символов");
        }

        this.value = url;
    }

    public static ProductPreviewImageUrl of(String url) {
        return new ProductPreviewImageUrl(url);
    }

    @Override
    public String toString() {
        return value;
    }
}
