package com.surofu.madeinrussia.core.model.vendorDetails.phoneNumber;

import com.surofu.madeinrussia.application.exception.LocalizedValidationException;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorPhoneNumberPhoneNumber implements Serializable {

    @Column(name = "phone_number", nullable = false)
    private String value;

    private VendorPhoneNumberPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || StringUtils.trimToNull(phoneNumber) == null) {
            throw new LocalizedValidationException("validation.phone_number.empty");
        }

        if (phoneNumber.length() < 7) {
            throw new LocalizedValidationException("validation.phone_number.min_length");
        }

        if (phoneNumber.length() > 255) {
            throw new LocalizedValidationException("validation.phone_number.max_length");
        }

        this.value = phoneNumber;
    }

    public static VendorPhoneNumberPhoneNumber of(String phoneNumber) {
        return new VendorPhoneNumberPhoneNumber(phoneNumber);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
