package com.surofu.madeinrussia.core.model.vendorDetails;

import com.surofu.madeinrussia.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorDetailsDescription implements Serializable {

    @Column(name = "description", nullable = false)
    private String value;

    private VendorDetailsDescription(String text) {
        if (text != null && text.length() > 20_000) {
            throw new LocalizedValidationException("validation.vendor.description.max_length");
        }

        this.value = Objects.requireNonNullElse(text, "");
    }

    public static VendorDetailsDescription of(String text) {
        return new VendorDetailsDescription(text);
    }

    @Override
    public String toString() {
        return value;
    }
}
