package com.surofu.madeinrussia.core.model.advertisement;

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
public final class AdvertisementSubtitle implements Serializable {

    @Column(name = "subtitle", nullable = false)
    private String value;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "subtitle_translations", nullable = false, columnDefinition = "hstore")
    private String translations;

    private AdvertisementSubtitle(String subtitle) {
        if (subtitle == null || subtitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Subtitle cannot be null or empty.");
        }

        if (subtitle.length() > 255) {
            throw new IllegalArgumentException("Subtitle cannot be longer than 255 characters.");
        }

        this.value = subtitle;
    }

    public static AdvertisementSubtitle of(String title) {
        return new AdvertisementSubtitle(title);
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
