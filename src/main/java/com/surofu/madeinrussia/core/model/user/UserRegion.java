package com.surofu.madeinrussia.core.model.user;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class UserRegion implements Serializable {
    private String region;

    private UserRegion(String region) {
        this.region = region;
    }

    public static UserRegion of(String region) {
        return new UserRegion(region);
    }
}
