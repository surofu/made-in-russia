package com.surofu.exporteru.core.model.vendorDetails.country;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.application.exception.LocalizedValidationException;
import com.surofu.exporteru.application.utils.HstoreParser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorCountryName implements Serializable {

    @Column(name = "name", nullable = false)
    private String value;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "name_translations", nullable = false, columnDefinition = "hstore")
    private String translations = HstoreParser.toString(HstoreTranslationDto.empty());

    private VendorCountryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.vendor.country.name.empty");
        }

        if (name.length() > 255) {
            throw new LocalizedValidationException("validation.vendor.country.name.max_length");
        }

        this.value = name;
    }

    public static VendorCountryName of(String name) {
        return new VendorCountryName(name);
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
