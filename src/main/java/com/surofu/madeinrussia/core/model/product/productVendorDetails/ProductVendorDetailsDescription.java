package com.surofu.madeinrussia.core.model.product.productVendorDetails;

import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.utils.HstoreParser;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductVendorDetailsDescription implements Serializable {

    @Column(name = "main_description", nullable = false, columnDefinition = "text")
    private String mainDescription;

    @Column(name = "further_description", columnDefinition = "text")
    private String furtherDescription;

    @ColumnTransformer(write = "?::hstore")
    @Column(name = "main_description_translations", nullable = false, columnDefinition = "hstore")
    private String mainDescriptionTranslations;

    @ColumnTransformer(write = "?::hstore")
    @Column(name = "further_description_translations", nullable = false, columnDefinition = "hstore")
    private String furtherDescriptionTranslations;

    private ProductVendorDetailsDescription(String mainDescription, String furtherDescription) {
        if (mainDescription == null || mainDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Главнове описание информации о продавце в товаре не может быть пустой");
        }

        this.mainDescription = mainDescription;
        this.furtherDescription = furtherDescription;
    }

    public static ProductVendorDetailsDescription of(String mainDescription, String furtherDescription) {
        return new ProductVendorDetailsDescription(mainDescription, furtherDescription);
    }

    public HstoreTranslationDto getMainDescriptionTranslations() {
        return HstoreParser.fromString(mainDescriptionTranslations);
    }

    public void setMainDescriptionTranslations(HstoreTranslationDto mainDescriptionTranslations) {
        this.mainDescriptionTranslations = HstoreParser.toString(mainDescriptionTranslations);
    }

    public HstoreTranslationDto getFurtherDescriptionTranslations() {
        return HstoreParser.fromString(furtherDescriptionTranslations);
    }

    public void setFurtherDescriptionTranslations(HstoreTranslationDto furtherDescriptionTranslations) {
        this.furtherDescriptionTranslations = HstoreParser.toString(furtherDescriptionTranslations);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", mainDescription, furtherDescription);
    }
}
