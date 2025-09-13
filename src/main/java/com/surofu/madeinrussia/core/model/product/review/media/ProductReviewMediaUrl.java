package com.surofu.madeinrussia.core.model.product.review.media;

import com.surofu.madeinrussia.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductReviewMediaUrl implements Serializable {

    @Column(name = "url", nullable = false, columnDefinition = "text")
    private String value;

    private ProductReviewMediaUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.external_link.empty");
        }

        if (url.length() >= 20_000) {
            throw new LocalizedValidationException("validation.external_link.max_length");
        }

        this.value = url;
    }

    public static ProductReviewMediaUrl of(String url) {
        return new ProductReviewMediaUrl(url);
    }

    @Override
    public String toString() {
        return value;
    }
}
