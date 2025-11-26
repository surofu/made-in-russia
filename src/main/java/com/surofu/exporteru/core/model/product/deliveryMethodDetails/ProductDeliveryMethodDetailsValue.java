package com.surofu.exporteru.core.model.product.deliveryMethodDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductDeliveryMethodDetailsValue implements Serializable {

    @Column(name = "value", nullable = false)
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "value_translations")
    private Map<String, String> translations;

    public ProductDeliveryMethodDetailsValue(String value, Map<String, String> translations) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Срок способа доставки товара не может быть пустым");
        }

        this.value = value;
        this.translations = translations;
    }

    public String getLocalizedValue() {
        if (translations == null || translations.isEmpty()) {
            return Objects.requireNonNullElse(value, "");
        }

        Locale locale = LocaleContextHolder.getLocale();
        return translations.getOrDefault(locale.getLanguage(), Objects.requireNonNullElse(value, ""));
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDeliveryMethodDetailsValue productDeliveryMethodDetailsValue)) return false;
        return Objects.equals(value, productDeliveryMethodDetailsValue.value);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
