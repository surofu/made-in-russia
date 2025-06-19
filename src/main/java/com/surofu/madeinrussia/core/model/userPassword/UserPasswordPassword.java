package com.surofu.madeinrussia.core.model.userPassword;

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
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        if (password.length() < 4) {
            throw new IllegalArgumentException("Пароль не может быть менее 4 символов");
        }

        if (password.length() >= 10_000) {
            throw new IllegalArgumentException("Пароль не может быть больше 10,000 символов");
        }

        return new UserPasswordPassword(password);
    }

    @Override
    public String toString() {
        return value;
    }
}