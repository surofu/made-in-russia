package com.surofu.madeinrussia.core.model.user.password;

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
public final class UserPasswordPassword implements Serializable {

    @Column(name = "password", nullable = false, columnDefinition = "text")
    private String value;

    private UserPasswordPassword(String password) {
        this.value = password;
    }

    public static UserPasswordPassword of(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.password.empty");
        }

        if (password.length() < 4) {
            throw new IllegalArgumentException("validation.password.min_length");
        }

        if (password.length() > 10_000) {
            throw new IllegalArgumentException("validation.password.max_length");
        }

        return new UserPasswordPassword(password);
    }

    @Override
    public String toString() {
        return value;
    }
}