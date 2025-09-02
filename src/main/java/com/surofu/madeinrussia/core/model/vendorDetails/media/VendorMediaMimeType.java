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
public class VendorMediaMimeType implements Serializable {

    @Column(name = "mime_type", nullable = false)
    private String value;

    private VendorMediaMimeType(String type) {
        if (StringUtils.trimToNull(type) == null) {
            throw new LocalizedValidationException("validation.media.mime_type.empty");
        }

        if (type.trim().length() > 255) {
            throw new LocalizedValidationException("validation.media.mime_type.max_length");
        }

        this.value = type.trim();
    }

    public static VendorMediaMimeType of(String type) {
        return new VendorMediaMimeType(type);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
