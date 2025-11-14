package com.surofu.exporteru.core.model.advertisement;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.application.exception.LocalizedValidationException;
import com.surofu.exporteru.application.utils.HstoreParser;
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
public final class AdvertisementTitle implements Serializable {

    @Column(name = "title", nullable = false)
    private String value;

    // TODO: AdvertisementTitle Translation. Hstore -> Jsonb
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "title_translations", nullable = false, columnDefinition = "hstore")
    private String translations = HstoreParser.toString(HstoreTranslationDto.empty());

    private AdvertisementTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.title.empty");
        }

        if (title.length() > 255) {
            throw new LocalizedValidationException("validation.title.max_length");
        }

        this.value = title;
    }

    public static AdvertisementTitle of(String title) {
        return new AdvertisementTitle(title);
    }

    public HstoreTranslationDto getTranslations() {
        return HstoreParser.fromString(translations);
    }

    public void setTranslations(HstoreTranslationDto translations) {
        this.translations = HstoreParser.toString(translations);
    }

    @Override
    public String toString() {
        return value;
    }
}
