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

    @Column(name = "rating", nullable = false, columnDefinition = "int default 1")
    private Integer value = 1;

    private ProductReviewRating(Integer rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Рейтинг отзыва должен быть между 0 и 5");
        }

        this.value = rating == 0 ? 1 : rating;
    }

    public static ProductReviewRating of(Integer rating) {
        return new ProductReviewRating(rating);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
