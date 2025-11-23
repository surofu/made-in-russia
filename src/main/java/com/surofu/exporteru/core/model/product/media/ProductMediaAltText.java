package com.surofu.exporteru.core.model.product.media;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductMediaAltText implements Serializable {

    @Column(name = "alt_text", nullable = false)
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "alt_text_translations")
    private Map<String, String> translations = new HashMap<>();

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
