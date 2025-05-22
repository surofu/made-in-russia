package com.surofu.madeinrussia.core.model.product.productReview.productReviewMedia;

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

    @Column(nullable = false)
    private Integer position;

    private ProductReviewMediaMediaPosition(Integer position) {
        this.position = position;
    }

    public static ProductReviewMediaMediaPosition of(Integer position) {
        return new ProductReviewMediaMediaPosition(position);
    }

    @Override
    public String toString() {
        return position.toString();
    }
}
