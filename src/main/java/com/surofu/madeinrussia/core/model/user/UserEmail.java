package com.surofu.madeinrussia.core.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor
public final class UserEmail implements Serializable {

    @Column(unique = true, nullable = false)
    private String email;

    private UserEmail(String email) {
        this.email = email;
    }

    public static UserEmail of(String email) {
        return new UserEmail(email);
    }

    @Override
    public String toString() {
        return email;
    }
}
