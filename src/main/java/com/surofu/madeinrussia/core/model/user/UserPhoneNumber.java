package com.surofu.madeinrussia.core.model.user;

import com.surofu.madeinrussia.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class UserPhoneNumber implements Serializable {

    @Column(name = "phone_number", nullable = false, unique = true)
    private String value;

    private UserPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
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
