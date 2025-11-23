package com.surofu.exporteru.core.model.vendorDetails.country;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.*;

import java.io.Serializable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorCountryName implements Serializable {

    @Column(name = "name", nullable = false)
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "name_translations")
    private Map<String, String> translations = new HashMap<>();

    private VendorCountryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.vendor.country.name.empty");
        }

        if (name.length() > 255) {
            throw new LocalizedValidationException("validation.vendor.country.name.max_length");
        }

        this.value = name;
    }

    public String getLocalizedValue(Locale locale) {
        if (translations == null || translations.isEmpty()) {
            return Objects.requireNonNullElse(value, "");
        }
        return translations.getOrDefault(locale.getLanguage(), Objects.requireNonNullElse(value, ""));
    }

    public static VendorCountryName of(String name) {
        return new VendorCountryName(name);
    }

    @Override
    public String toString() {
        return value;
    }
}
