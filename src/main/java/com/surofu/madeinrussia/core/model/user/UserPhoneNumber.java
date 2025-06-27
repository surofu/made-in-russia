package com.surofu.madeinrussia.core.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.regex.Pattern;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class UserPhoneNumber implements Serializable {

    @Column(name = "phone_number", nullable = false, unique = true)
    private String value;

    private UserPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Номер телефона не может быть пустым");
        }

        if (phoneNumber.length() < 7) {
            throw new IllegalArgumentException("Номер телефона не может быть меньше 7 символов");
        }

        if (phoneNumber.length() > 255) {
            throw new IllegalArgumentException("Номер телефона не может быть больше 255 символов");
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
}
