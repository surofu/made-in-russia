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
public final class UserLogin implements Serializable {

    @Column(name = "login", nullable = false)
    private String value;

    private UserLogin(String login) {
        this.value = login;
    }

    public static UserLogin of(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.login.empty");
        }

        if (login.length() < 2) {
            throw new LocalizedValidationException("validation.login.min_length");
        }

        if (login.length() > 255) {
            throw new LocalizedValidationException("validation.login.max_length");
        }

        return new UserLogin(login);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value != null && value.equals(((UserLogin) o).value);
    }
}
