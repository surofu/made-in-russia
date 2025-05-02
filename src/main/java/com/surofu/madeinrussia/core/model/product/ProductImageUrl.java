package com.surofu.madeinrussia.core.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductImageUrl implements Serializable {
    @Column(name = "imageUrl")
    private String value;

    private ProductImageUrl(String imageUrl) {
        this.value = value;
    }

    public static ProductImageUrl of(String value) {
        return new ProductImageUrl(value);
    }
}
