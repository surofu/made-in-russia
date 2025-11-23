package com.surofu.exporteru.core.model.vendorDetails.productCategory;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
public final class VendorProductCategoryName implements Serializable {

    @Column(name = "name", nullable = false)
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "name_translations")
    private Map<String, String> translations = new HashMap<>();

    private VendorProductCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.vendor.product_category.name.empty");
        }

        if (name.length() > 255) {
            throw new LocalizedValidationException("validation.vendor.product_category.name.max_length");
        }

        this.value = name;
    }

    public static VendorProductCategoryName of(String name) {
        return new VendorProductCategoryName(name);
    }

    public String getLocalizedValue(Locale locale) {
        if (translations == null || translations.isEmpty()) {
            return Objects.requireNonNullElse(value, "");
        }
        return translations.getOrDefault(locale.getLanguage(), Objects.requireNonNullElse(value, ""));
    }

    @Override
    public String toString() {
        return value;
    }
}
