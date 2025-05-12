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

    @Column(unique = true)
    private String login;

    private UserLogin(String login) {
        this.login = login;
    }

    public static UserLogin of(String login) {
        return new UserLogin(login);
    }
}
