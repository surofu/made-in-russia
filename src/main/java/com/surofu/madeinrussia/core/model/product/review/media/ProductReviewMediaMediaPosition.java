package com.surofu.madeinrussia.core.model.product.review.media;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductReviewMediaMediaPosition implements Serializable {

    @Column(name = "position", nullable = false, columnDefinition = "int default 0")
    private Integer value = 0;

    private ProductReviewMediaMediaPosition(Integer position) {
        if (position < 0) {
            throw new IllegalArgumentException("vendor.media.invalid_position");
        }

        this.value = position;
    }

    public static ProductReviewMediaMediaPosition of(Integer position) {
        return new ProductReviewMediaMediaPosition(position);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
