package com.surofu.madeinrussia.core.model.product.vendorDetails.productVendorDetailsMedia;

import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.utils.HstoreParser;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductVendorDetailsMediaImage implements Serializable {

    @Column(name = "url", nullable = false, columnDefinition = "text")
    private String url;

    @Column(name = "alt_text")
    private String altText;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "alt_text_translations", nullable = false, columnDefinition = "hstore")
    private String altTextTranslations;

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

    public HstoreTranslationDto getAltTextTranslations() {
        return HstoreParser.fromString(altTextTranslations);
    }

    public void setAltTextTranslations(HstoreTranslationDto translations) {
        this.altTextTranslations = HstoreParser.toString(translations);
    }

    @Override
    public String toString() {
        return "ProductVendorDetailsMediaImage [url=" + url + ", altText=" + altText + "]";
    }
}
