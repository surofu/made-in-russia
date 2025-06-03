package com.surofu.madeinrussia.core.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductTitle implements Serializable {

    @Column(name = "title", nullable = false)
    private String value;

    private ProductTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Название товара не должно быть пустым");
        }

        if (title.length() > 255) {
            throw new IllegalArgumentException("Название товара не должно быть больше 255 символов");
        }

        this.value = title;
    }

    public static ProductTitle of(String title) {
        return new ProductTitle(title);
    }

    @Override
    public String toString() {
        return value;
    }
}
