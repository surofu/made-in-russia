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
public final class UserPhoneNumber implements Serializable {

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    private UserPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public static UserPhoneNumber of(String phoneNumber) {
        return new UserPhoneNumber(phoneNumber);
    }
}
