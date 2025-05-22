package com.surofu.madeinrussia.core.model.product.productMedia;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductMediaAltText implements Serializable {

    @Column(nullable = false)
    private String altText;

    private ProductMediaAltText(String altText) {
        this.altText = altText;
    }

    public static ProductMediaAltText of(String altText) {
        return new ProductMediaAltText(altText);
    }

    @Override
    public String toString() {
        return altText;
    }
}
