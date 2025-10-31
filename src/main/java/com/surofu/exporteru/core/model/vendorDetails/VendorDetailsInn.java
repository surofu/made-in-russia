package com.surofu.exporteru.core.model.vendorDetails;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorDetailsInn implements Serializable {

    @Column(name = "inn", nullable = false, unique = true)
    private String value;

    private VendorDetailsInn(String inn) {
        if (inn == null || inn.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.vendor.inn.empty");
        }

        if (inn.length() < 7) {
            throw new LocalizedValidationException("validation.vendor.inn.min_length");
        }

        if (inn.length() > 255) {
            throw new LocalizedValidationException("validation.vendor.inn.max_length");
        }

        this.value = inn;
    }

    public static VendorDetailsInn of(String inn) {
        return new VendorDetailsInn(inn);
    }

    @Override
    public String toString() {
        return value;
    }
}
