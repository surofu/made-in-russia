package com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry;

import com.surofu.madeinrussia.application.dto.HstoreTranslationDto;
import com.surofu.madeinrussia.application.utils.HstoreParser;
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

    @ColumnTransformer(write = "?::hstore")
    @Column(name = "name_translations", nullable = false, columnDefinition = "hstore")
    private String translations;

    private VendorCountryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название страны продавца не может быть пустым");
        }

        if (name.length() > 255) {
            throw new IllegalArgumentException("Название страны продавца не может быть больше 255 символов");
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
