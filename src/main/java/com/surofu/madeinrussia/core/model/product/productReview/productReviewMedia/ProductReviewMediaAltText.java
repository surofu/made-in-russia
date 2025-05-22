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
public final class ProductReviewMediaAltText implements Serializable {

    @Column(nullable = false)
    private String altText;

    private ProductReviewMediaAltText(String altText) {
        this.altText = altText;
    }

    public static ProductReviewMediaAltText of(String altText) {
        return new ProductReviewMediaAltText(altText);
    }

    @Override
    public String toString() {
        return altText;
    }
}
