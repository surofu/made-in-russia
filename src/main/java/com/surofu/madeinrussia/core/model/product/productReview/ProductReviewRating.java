package com.surofu.madeinrussia.core.model.product.productReview;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductReviewRating implements Serializable {

    @Column(nullable = false)
    private Integer rating;

    private ProductReviewRating(Integer rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        this.rating = rating;
    }

    public static ProductReviewRating of(Integer rating) {
        return new ProductReviewRating(rating);
    }

    @Override
    public String toString() {
        return rating.toString();
    }
}
