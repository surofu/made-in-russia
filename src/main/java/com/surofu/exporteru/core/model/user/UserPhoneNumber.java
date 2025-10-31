package com.surofu.exporteru.core.model.user;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
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
public final class UserPhoneNumber implements Serializable {

    @Column(name = "phone_number", unique = true)
    private String value;

    private UserPhoneNumber(String phoneNumber) {
       if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
           if (phoneNumber.length() < 7) {
               throw new LocalizedValidationException("validation.phone_number.min_length");
           }

           if (phoneNumber.length() > 255) {
               throw new LocalizedValidationException("validation.phone_number.max_length");
           }
       }

        this.value = StringUtils.trimToNull(phoneNumber);
    }

    public static UserPhoneNumber of(String phoneNumber) {
        return new UserPhoneNumber(phoneNumber);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value != null && value.equals(((UserPhoneNumber) o).value);
    }
}
