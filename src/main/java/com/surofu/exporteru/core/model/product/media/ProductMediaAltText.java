package com.surofu.exporteru.core.model.product.media;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.application.utils.HstoreParser;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductMediaAltText implements Serializable {

    @Column(name = "alt_text", nullable = false)
    private String value;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "alt_text_translations", nullable = false, columnDefinition = "hstore")
    private String translations = HstoreParser.toString(HstoreTranslationDto.empty());

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

    public HstoreTranslationDto getTranslations() {
        return HstoreParser.fromString(translations);
    }

    public void setTranslations(HstoreTranslationDto translations) {
        this.translations = HstoreParser.toString(translations);
    }

    @Override
    public String toString() {
        return value;
    }
}
