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
public final class ProductMediaUrl implements Serializable {

    @Column(nullable = false)
    private String url;

    private ProductMediaUrl(String url) {
        this.url = url;
    }

    public static ProductMediaUrl of(String url) {
        return new ProductMediaUrl(url);
    }

    @Override
    public String toString() {
        return url;
    }
}
