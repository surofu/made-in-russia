package com.surofu.madeinrussia.core.model.user;

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
            throw new IllegalArgumentException("Почта не может быть пустой");
        }

        if (email.length() > 255) {
            throw new IllegalArgumentException("Почта не может быть больше 255 символов");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Почта должна соответствовать выражению ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        }

        this.value = email;
    }

    public static UserEmail of(String email) {
        return new UserEmail(email);
    }

    @Override
    public String toString() {
        return value;
    }
}
