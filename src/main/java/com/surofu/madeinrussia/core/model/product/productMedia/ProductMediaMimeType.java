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
public final class ProductMediaMimeType implements Serializable {

    @Column(nullable = false)
    private String mimeType;

    private ProductMediaMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public static ProductMediaMimeType of(String mimeType) {
        return new ProductMediaMimeType(mimeType);
    }

    @Override
    public String toString() {
        return mimeType;
    }
}
