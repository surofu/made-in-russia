package com.surofu.madeinrussia.core.model.product.productCharacteristic;

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
public final class ProductCharacteristicValue implements Serializable {

    @Column(name = "value", nullable = false)
    private String value;

    @ColumnTransformer(write = "?::hstore")
    @Column(name = "value_translations", nullable = false, columnDefinition = "hstore")
    private String translations;

    private ProductCharacteristicValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Значение характеристики не может быть пустой");
        }

        if (value.length() > 255) {
            throw new IllegalArgumentException("Значение характеристики не может быть больше 255 символов");
        }

        this.value = value;
    }

    public static ProductCharacteristicValue of(String value) {
        return new ProductCharacteristicValue(value);
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
