package com.surofu.exporteru.core.model.product.media;

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
public final class ProductMediaUrl implements Serializable {

    @Column(name = "url", nullable = false, columnDefinition = "text")
    private String value;

    private ProductMediaUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.media.url.empty");
        }

        if (url.length() >= 20_000) {
            throw new LocalizedValidationException("validation.media.url.max_length");
        }

        this.value = url;
    }

    public static ProductMediaUrl of(String url) {
        return new ProductMediaUrl(url);
    }

    @Override
    public String toString() {
        return value;
    }
}
