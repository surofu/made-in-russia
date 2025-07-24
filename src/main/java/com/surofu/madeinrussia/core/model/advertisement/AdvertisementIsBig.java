package com.surofu.madeinrussia.core.model.advertisement;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class AdvertisementIsBig implements Serializable {

    @Column(name = "is_big", nullable = false, columnDefinition = "boolean default false")
    private Boolean value = false;

    private AdvertisementIsBig(Boolean state) {
        this.value = state != null && state;
    }

    public static AdvertisementIsBig of(Boolean state) {
        return new AdvertisementIsBig(state);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
