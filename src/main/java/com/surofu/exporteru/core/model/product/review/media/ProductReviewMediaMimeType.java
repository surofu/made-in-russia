package com.surofu.exporteru.core.model.product.review.media;

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
public final class ProductReviewMediaMimeType implements Serializable {

    @Column(name = "mime_type", nullable = false)
    private String value;

    private ProductReviewMediaMimeType(String mimeType) {
        if (mimeType == null || mimeType.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.media.mime_type.empty");
        }

        if (mimeType.length() > 255) {
            throw new LocalizedValidationException("validation.media.mime_type.max_length");
        }

        this.value = mimeType;
    }

    public static ProductReviewMediaMimeType of(String mimeType) {
        return new ProductReviewMediaMimeType(mimeType);
    }

    @Override
    public String toString() {
        return value;
    }
}
