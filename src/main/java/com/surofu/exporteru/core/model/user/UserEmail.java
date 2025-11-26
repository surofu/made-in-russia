package com.surofu.exporteru.core.model.user;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor
public final class UserEmail implements Serializable {

    @Column(name = "email", nullable = false, unique = true)
    private String value;

    private UserEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.email.empty");
        }

        if (email.length() > 255) {
            throw new LocalizedValidationException("validation.email.length");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new LocalizedValidationException("validation.email.format");
        }

        this.value = email.trim().toLowerCase();
    }

    public static UserEmail of(String email) {
        return new UserEmail(email);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEmail userEmail)) return false;
        return Objects.equals(value, userEmail.value);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
