package com.surofu.madeinrussia.core.model.user;

import com.surofu.madeinrussia.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.regex.Pattern;

@Getter
@Embeddable
@NoArgsConstructor
public final class UserEmail implements Serializable {

    @Transient
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    @Column(name = "email", nullable = false, unique = true)
    private String value;

    private UserEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.email.empty");
        }

        if (email.length() > 255) {
            throw new LocalizedValidationException("validation.email.length");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new LocalizedValidationException("validation.email.format");
        }

        this.value = email.toLowerCase();
    }

    public static UserEmail of(String email) {
        return new UserEmail(email);
    }

    public String getValue() {
        return value.toLowerCase();
    }

    @Override
    public String toString() {
        return value.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value != null && value.equals(((UserEmail) o).value);
    }
}
