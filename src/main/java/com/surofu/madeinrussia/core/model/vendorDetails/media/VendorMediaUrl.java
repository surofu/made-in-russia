package com.surofu.madeinrussia.core.model.vendorDetails.media;

import com.surofu.madeinrussia.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VendorMediaUrl implements Serializable {

    @Column(name = "url", nullable = false)
    private String value;

    private VendorMediaUrl(String url) {
        if (StringUtils.trimToNull(url) == null) {
            throw new LocalizedValidationException("validation.media.url.empty");
        }

        if (url.trim().length() > 20_000) {
            throw new LocalizedValidationException("validation.media.url.max_length");
        }

        this.value = url.trim();
    }

    public static VendorMediaUrl of(String url) {
        return new VendorMediaUrl(url);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
