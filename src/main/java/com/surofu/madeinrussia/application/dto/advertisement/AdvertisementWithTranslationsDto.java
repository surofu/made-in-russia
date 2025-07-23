package com.surofu.madeinrussia.application.dto.advertisement;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;
import com.surofu.madeinrussia.application.utils.HstoreParser;
import com.surofu.madeinrussia.core.model.advertisement.Advertisement;
import com.surofu.madeinrussia.infrastructure.persistence.advertisement.AdvertisementWithTranslationsView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class AdvertisementWithTranslationsDto implements Serializable {

    private Long id;

    private String title;

    private TranslationDto titleTranslations;

    private String subtitle;

    private TranslationDto subtitleTranslations;

    private String imageUrl;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    public static AdvertisementWithTranslationsDto of(Advertisement advertisement) {
        return AdvertisementWithTranslationsDto.builder()
                .id(advertisement.getId())
                .title(advertisement.getTitle().toString())
                .titleTranslations(TranslationDto.of(advertisement.getTitle().getTranslations()))
                .subtitle(advertisement.getSubtitle().toString())
                .subtitleTranslations(TranslationDto.of(advertisement.getSubtitle().getTranslations()))
                .imageUrl(advertisement.getImage().toString())
                .creationDate(advertisement.getCreationDate().getValue())
                .lastModificationDate(advertisement.getLastModificationDate().getValue())
                .build();
    }

    public static AdvertisementWithTranslationsDto of(AdvertisementWithTranslationsView view) {
        return AdvertisementWithTranslationsDto.builder()
                .id(view.getId())
                .title(view.getTitle())
                .titleTranslations(TranslationDto.of(HstoreParser.fromString(view.getTitleTranslations())))
                .subtitle(view.getSubtitle())
                .subtitleTranslations(TranslationDto.of(HstoreParser.fromString(view.getSubtitleTranslations())))
                .imageUrl(view.getImageUrl())
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}
