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
public final class AdvertisementLink implements Serializable {

    @Column(name = "link")
    private String value;

    private AdvertisementLink(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.external_link.empty");
        }

        if (url.length() > 20_000) {
            throw new LocalizedValidationException("validation.external_link.max_length");
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
