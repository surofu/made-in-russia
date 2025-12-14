package com.surofu.exporteru.core.model.product.review;

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
public final class ProductReviewRating implements Serializable {
    @Column(name = "rating", nullable = false, columnDefinition = "int default 1")
    private Integer value = 1;

    public ProductReviewRating(Integer rating) {
        if (rating < 0 || rating > 5) {
            throw new LocalizedValidationException("validation.product.review.rating.length");
        }
        this.value = rating == 0 ? 1 : rating;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductReviewRating that)) {
            return false;
        }
      return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
