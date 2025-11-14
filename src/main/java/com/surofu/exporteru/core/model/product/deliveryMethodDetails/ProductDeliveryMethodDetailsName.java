package com.surofu.exporteru.core.model.product.deliveryMethodDetails;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.application.utils.HstoreParser;
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
public final class ProductDeliveryMethodDetailsName implements Serializable {

    @Column(name = "name", nullable = false)
    private String value;

    // TODO: ProductDeliveryMethodDetailsName Translation. Hstore -> Jsonb
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "name_translations", nullable = false, columnDefinition = "hstore")
    private String translations = HstoreParser.toString(HstoreTranslationDto.empty());

    private ProductDeliveryMethodDetailsName(String name) {
        this.value = name;
    }

    public static ProductDeliveryMethodDetailsName of(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название способа доставки товара не может быть пустым");
        }

        return new ProductDeliveryMethodDetailsName(name);
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
