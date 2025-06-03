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
public final class UserRegion implements Serializable {

    @Column(name = "region", nullable = false)
    private String value;

    private UserRegion(String region) {
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("Регион не может быть пустым");
        }

        if (region.length() > 255) {
            throw new IllegalArgumentException("Регион не может быть больше 255 символов");
        }

        this.value = region;
    }

    public static UserRegion of(String region) {
        return new UserRegion(region);
    }

    @Override
    public String toString() {
        return value;
    }
}
