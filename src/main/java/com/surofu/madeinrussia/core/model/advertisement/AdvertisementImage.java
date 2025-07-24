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
public final class AdvertisementImage implements Serializable {

    @Column(name = "image_url", nullable = false)
    private String url;

    private AdvertisementImage(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Image url cannot be null or empty.");
        }

        if (url.length() > 255) {
            throw new IllegalArgumentException("Image url cannot be longer than 255 characters.");
        }

        this.url = url;
    }

    public static AdvertisementImage of(String title) {
        return new AdvertisementImage(title);
    }

    @Override
    public String toString() {
        return url;
    }
}
