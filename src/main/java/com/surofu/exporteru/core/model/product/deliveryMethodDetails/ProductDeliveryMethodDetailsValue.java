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
public final class ProductDeliveryMethodDetailsValue implements Serializable {

    @Column(name = "value", nullable = false)
    private String value;

    // TODO: ProductDeliveryMethodDetailsValue Translation. Hstore -> Jsonb
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "value_translations", nullable = false, columnDefinition = "hstore")
    private String translations = HstoreParser.toString(HstoreTranslationDto.empty());

    private ProductDeliveryMethodDetailsValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Срок способа доставки товара не может быть пустым");
        }

        this.value = value;
    }

    public static ProductDeliveryMethodDetailsValue of(String value) {
        return new ProductDeliveryMethodDetailsValue(value);
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
