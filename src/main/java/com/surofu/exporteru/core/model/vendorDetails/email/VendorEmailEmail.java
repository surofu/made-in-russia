package com.surofu.exporteru.core.model.vendorDetails.email;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.regex.Pattern;

@Generated
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorEmailEmail implements Serializable {

    @Transient
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    @Column(name = "email", nullable = false)
    private String value;

    private VendorEmailEmail(String value) {
        if (value == null || StringUtils.trimToNull(value) == null) {
            throw new LocalizedValidationException("validation.email.empty");
        }

        if (value.length() > 255) {
            throw new LocalizedValidationException("validation.email.length");
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new LocalizedValidationException("validation.email.format");
        }

        this.value = value.toLowerCase();
    }

    public static VendorEmailEmail of(String email) {
        return new VendorEmailEmail(email);
    }

    @Override
    public String toString() {
        return value;
    }
}
