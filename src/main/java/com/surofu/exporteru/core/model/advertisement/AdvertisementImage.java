package com.surofu.exporteru.core.model.advertisement;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
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

    @Column(name = "image_url", nullable = false, columnDefinition = "text")
    private String url;

    private AdvertisementImage(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.image_url.empty");
        }

        if (url.length() > 20_000) {
            throw new LocalizedValidationException("validation.image_url.max_length");
        }

        this.url = url;
    }

    public static AdvertisementImage of(String url) {
        return new AdvertisementImage(url);
    }

    @Override
    public String toString() {
        return url;
    }
}
