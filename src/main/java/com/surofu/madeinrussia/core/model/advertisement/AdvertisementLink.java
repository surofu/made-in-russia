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
public final class AdvertisementLink implements Serializable {

    @Column(name = "link")
    private String value;

    private AdvertisementLink(String url) {
        if (url.length() > 20_000) {
            throw new IllegalArgumentException("Link url cannot be longer than 20,000 characters");
        }

        this.value = url;
    }

    public static AdvertisementLink of(String url) {
        return new AdvertisementLink(url);
    }

    @Override
    public String toString() {
        return value;
    }
}
