package com.surofu.madeinrussia.core.model.product.characteristic;

import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.utils.HstoreParser;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductCharacteristicName implements Serializable {

    @Column(name = "name", nullable = false)
    private String value;

    @ColumnTransformer(write = "?::hstore")
    @Column(name = "name_translations", nullable = false, columnDefinition = "hstore")
    private String translations;

    private ProductCharacteristicName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название характеристики не может быть пустой");
        }

        if (name.length() > 255) {
            throw new IllegalArgumentException("Название характеристики не может быть больше 255 символов");
        }

        this.value = name;
    }

    public static ProductCharacteristicName of(String name) {
        return new ProductCharacteristicName(name);
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
