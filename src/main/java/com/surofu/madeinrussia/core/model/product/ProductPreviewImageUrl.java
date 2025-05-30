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
public final class ProductPreviewImageUrl implements Serializable {

    @Column(nullable = false, columnDefinition = "text")
    private String previewImageUrl;

    private ProductPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }

    public static ProductPreviewImageUrl of(String url) {
        return new ProductPreviewImageUrl(url);
    }

    @Override
    public String toString() {
        return previewImageUrl;
    }
}
