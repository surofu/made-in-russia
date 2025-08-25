package com.surofu.madeinrussia.core.model.vendorDetails.site;

import com.surofu.madeinrussia.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorSiteUrl implements Serializable {

    @Column(name = "url", nullable = false)
    private String value;

    private VendorSiteUrl(String url) {
        if (url != null && StringUtils.trimToNull(url) != null && url.length() > 255) {
            throw new LocalizedValidationException("validation.vendor.site.max_length");
        }

        this.value = Objects.requireNonNullElse(url, "");
    }

    public static VendorSiteUrl of(String url) {
        return new VendorSiteUrl(url);
    }

    @Override
    public String toString() {
        return value;
    }
}
