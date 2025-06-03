package com.surofu.madeinrussia.core.model.user;

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

    @Column(name = "login", nullable = false, unique = true)
    private String value;

    private UserLogin(String login) {
        this.value = login;
    }

    public static UserLogin of(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }

        if (login.length() < 3) {
            throw new IllegalArgumentException("Логин не может быть менее 3 символов");
        }

        if (login.length() > 255) {
            throw new IllegalArgumentException("Логин не может быть больше 255 символов");
        }

        return new UserLogin(login);
    }

    @Override
    public String toString() {
        return value;
    }
}
