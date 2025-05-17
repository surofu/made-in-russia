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

    @Column(nullable = false)
    private String password;

    private UserPasswordPassword(String password) {
        this.password = password;
    }

    public static UserPasswordPassword of(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        if (password.isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        if (password.length() < 4) {
            throw new IllegalArgumentException("Пароль не может быть менее 4 символов");
        }

        return new UserPasswordPassword(password);
    }

    @Override
    public String toString() {
        return password;
    }
}