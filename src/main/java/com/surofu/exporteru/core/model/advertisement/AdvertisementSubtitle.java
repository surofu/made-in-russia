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
public final class AdvertisementSubtitle implements Serializable {

    @Column(name = "subtitle", nullable = false)
    private String value;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "subtitle_translations", nullable = false, columnDefinition = "hstore")
    private String translations = HstoreParser.toString(HstoreTranslationDto.empty());

    private AdvertisementSubtitle(String subtitle) {
        if (subtitle == null || subtitle.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.subtitle.empty");
        }

        if (subtitle.length() > 255) {
            throw new LocalizedValidationException("validation.subtitle.max_length");
        }

        this.value = subtitle;
    }

    public static AdvertisementSubtitle of(String subtitle) {
        return new AdvertisementSubtitle(subtitle);
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
