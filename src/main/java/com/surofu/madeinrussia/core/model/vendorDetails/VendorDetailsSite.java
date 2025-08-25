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
public final class VendorDetailsSite implements Serializable {

    @Column(name = "site", nullable = false)
    private String value;

    private VendorDetailsSite(String url) {
        if (url != null && url.length() > 255) {
            throw new LocalizedValidationException("validation.vendor.site.max_length");
        }

        this.value = Objects.requireNonNullElse(url, "");
    }

    public static VendorDetailsSite of(String url) {
        return new VendorDetailsSite(url);
    }

    @Override
    public String toString() {
        return value;
    }
}
