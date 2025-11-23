package com.surofu.exporteru.core.model.vendorDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
public final class VendorDetailsAddress implements Serializable {

    @Column(name = "address")
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "address_translations", nullable = false, columnDefinition = "hstore")
    private Map<String, String> translations = new HashMap<>();

    private VendorDetailsAddress(String address) {
        this.value = address;
    }

    public String getLocalizedValue(Locale locale) {
        if (translations == null || translations.isEmpty()) {
            return value;
        }
        return translations.getOrDefault(locale.getLanguage(), value);
    }

    public static VendorDetailsAddress of(String address) {
        return new VendorDetailsAddress(address);
    }

    @Override
    public String toString() {
        return value;
    }
}
