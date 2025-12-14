package com.surofu.exporteru.core.model.product.review.media;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductReviewMediaAltText implements Serializable {
    @Column(name = "alt_text", nullable = false)
    private String value;

    public ProductReviewMediaAltText(String altText) {
        if (altText == null || altText.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.media.alt_text.empty");
        }
        if (altText.length() > 255) {
            throw new LocalizedValidationException("validation.media.alt_text.max_length");
        }
        this.value = altText;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductReviewMediaAltText that)) {
            return false;
        }
      return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
