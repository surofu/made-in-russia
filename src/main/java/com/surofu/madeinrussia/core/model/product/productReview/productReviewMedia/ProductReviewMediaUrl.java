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
public final class ProductReviewMediaUrl implements Serializable {

    @Column(nullable = false, columnDefinition = "text")
    private String url;

    private ProductReviewMediaUrl(String url) {
        this.url = url;
    }

    public static ProductReviewMediaUrl of(String url) {
        return new ProductReviewMediaUrl(url);
    }

    @Override
    public String toString() {
        return url;
    }
}
