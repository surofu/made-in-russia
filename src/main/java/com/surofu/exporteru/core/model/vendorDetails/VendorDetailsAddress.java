package com.surofu.exporteru.core.model.vendorDetails;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.application.utils.HstoreParser;
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
public final class VendorDetailsAddress implements Serializable {

    @Column(name = "address")
    private String value;

    // TODO: VendorDetailsAddress Translation. Hstore -> Jsonb
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "address_translations", nullable = false, columnDefinition = "hstore")
    private String translations = HstoreParser.toString(HstoreTranslationDto.empty());

    private VendorDetailsAddress(String address) {
        this.value = address;
    }

    public static VendorDetailsAddress of(String address) {
        return new VendorDetailsAddress(address);
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
