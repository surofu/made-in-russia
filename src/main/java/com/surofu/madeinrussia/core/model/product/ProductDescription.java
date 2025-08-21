package com.surofu.madeinrussia.core.model.product;

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
public final class ProductDescription implements Serializable {

    @Column(name = "main_description", nullable = false, columnDefinition = "text")
    private String mainDescription;

    @Column(name = "further_description", columnDefinition = "text")
    private String furtherDescription;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "main_description_translations", nullable = false, columnDefinition = "hstore")
    private String mainDescriptionTranslations;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "further_description_translations", columnDefinition = "hstore")
    private String furtherDescriptionTranslations;

    private ProductDescription(String mainDescription, String furtherDescription) {
        if (mainDescription == null || mainDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Главное описание не может быть пустым");
        }

        if (mainDescription.length() >= 50_000) {
            throw new IllegalArgumentException("Главное описание не может быть больше 50,000 символов");
        }

        if (furtherDescription != null && furtherDescription.length() >= 20_000) {
            throw new IllegalArgumentException("Второстепенное описание не может быть больше 20,000 символов");
        }

        this.mainDescription = mainDescription;
        this.furtherDescription = furtherDescription;
    }

    public HstoreTranslationDto getMainDescriptionTranslations() {
        return HstoreParser.fromString(this.mainDescriptionTranslations);
    }

    public void setMainDescriptionTranslations(HstoreTranslationDto translations) {
        this.mainDescriptionTranslations = HstoreParser.toString(translations);
    }

    public HstoreTranslationDto getFurtherDescriptionTranslations() {
        return HstoreParser.fromString(this.furtherDescriptionTranslations);
    }

    public void setFurtherDescriptionTranslations(HstoreTranslationDto translations) {
        this.furtherDescriptionTranslations = HstoreParser.toString(translations);
    }

    public static ProductDescription of(String mainDescription, String furtherDescription) {
        return new ProductDescription(mainDescription, furtherDescription);
    }

    @Override
    public String toString() {
        return mainDescription + " - " + furtherDescription;
    }
}
