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
public final class ProductReviewMediaMimeType implements Serializable {

    @Column(nullable = false)
    private String mimeType;

    private ProductReviewMediaMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public static ProductReviewMediaMimeType of(String mimeType) {
        return new ProductReviewMediaMimeType(mimeType);
    }

    @Override
    public String toString() {
        return mimeType;
    }
}
