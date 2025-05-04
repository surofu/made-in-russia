package com.surofu.madeinrussia.core.model.product;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductImageUrl implements Serializable {
    private String imageUrl;

    private ProductImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static ProductImageUrl of(String imageUrl) {
        return new ProductImageUrl(imageUrl);
    }
}
