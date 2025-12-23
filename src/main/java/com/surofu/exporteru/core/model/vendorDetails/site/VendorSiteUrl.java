package com.surofu.exporteru.core.model.vendorDetails.site;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
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

    public VendorSiteUrl(String url) {
        if (url != null && StringUtils.trimToNull(url) != null && url.length() > 255) {
            throw new LocalizedValidationException("validation.vendor.site.max_length");
        }
        this.value = Objects.requireNonNullElse(url, "");
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VendorSiteUrl that)) {
            return false;
        }
      return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
