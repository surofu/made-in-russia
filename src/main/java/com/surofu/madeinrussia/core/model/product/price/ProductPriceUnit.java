package com.surofu.madeinrussia.core.model.product.price;

import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.utils.HstoreParser;
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
public final class ProductPriceUnit implements Serializable {

    @Column(name = "quantity_unit", nullable = false)
    private String value;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "unit_translations", nullable = false, columnDefinition = "hstore")
    private String transltations;

    private ProductPriceUnit(String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("Единицы измерения цены товара не могут быть пустыми");
        }

        if (unit.length() > 255) {
            throw new IllegalArgumentException("Единицы измерения цены товара не могут быть больше 255 символов");
        }

        this.value = unit;
    }

    public static ProductPriceUnit of(String unit) {
        return new ProductPriceUnit(unit);
    }

    public HstoreTranslationDto getTranslations() {
        return HstoreParser.fromString(transltations);
    }

    public void setTranslations(HstoreTranslationDto translations) {
        this.transltations = HstoreParser.toString(translations);
    }

    @Override
    public String toString() {
        return value;
    }
}
