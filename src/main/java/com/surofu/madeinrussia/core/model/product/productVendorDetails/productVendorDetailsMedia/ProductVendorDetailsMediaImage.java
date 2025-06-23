package com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductVendorDetailsMediaImage implements Serializable {

    @Column(name = "url", nullable = false, columnDefinition = "text")
    private String url;

    @Column(name = "alt_text")
    private String altText;

    private ProductVendorDetailsMediaImage(String url, String altText) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Ссылка на изображение информации о продавце в товаре не может быть пустой");
        }

        this.url = url;
        this.altText = altText;
    }

    public static ProductVendorDetailsMediaImage of(String url, String altText) {
        return new ProductVendorDetailsMediaImage(url, altText);
    }

    @Override
    public String toString() {
        return "ProductVendorDetailsMediaImage [url=" + url + ", altText=" + altText + "]";
    }
}
