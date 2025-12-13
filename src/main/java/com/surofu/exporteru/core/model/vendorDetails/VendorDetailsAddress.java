package com.surofu.exporteru.core.model.vendorDetails;

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
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorDetailsAddress implements Serializable {

    @Column(name = "address")
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "address_translations", nullable = false, columnDefinition = "hstore")
    private Map<String, String> translations = new HashMap<>();

    private VendorDetailsAddress(String address) {
        this.value = address;
    }

    public String getLocalizedValue() {
        if (translations == null || translations.isEmpty()) {
            return Objects.requireNonNullElse(value, "");
        }
        Locale locale = LocaleContextHolder.getLocale();
        return translations.getOrDefault(locale.getLanguage(), Objects.requireNonNullElse(value, ""));
    }

    public static VendorDetailsAddress of(String address) {
        return new VendorDetailsAddress(address);
    }

    @Override
    public String toString() {
        return value;
    }
}
