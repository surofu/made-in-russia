package com.surofu.exporteru.application.dto.advertisement;

import com.surofu.exporteru.core.model.advertisement.Advertisement;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class AdvertisementWithTranslationsDto implements Serializable {
  private Long id;
  private String title;
  private Map<String, String> titleTranslations;
  private String subtitle;
  private Map<String, String> subtitleTranslations;
  private String thirdText;
  private Map<String, String> thirdTextTranslations;
  private String link;
  private String imageUrl;
  private Boolean isBig;
  private ZonedDateTime expirationDate;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  public static AdvertisementWithTranslationsDto of(Advertisement advertisement, Locale locale) {
    return AdvertisementWithTranslationsDto.builder()
        .id(advertisement.getId())
        .title(
            advertisement.getTitle() != null ? advertisement.getTitle().getLocalizedValue(locale) :
                "")
        .titleTranslations(
            advertisement.getTitle() != null ? advertisement.getTitle().getTranslations() :
                Collections.emptyMap())
        .subtitle(advertisement.getSubtitle() != null ?
            advertisement.getSubtitle().getLocalizedValue(locale) : "")
        .subtitleTranslations(
            advertisement.getSubtitle() != null ? advertisement.getSubtitle().getTranslations() :
                Collections.emptyMap())
        .thirdText(advertisement.getThirdText() != null ?
            advertisement.getThirdText().getLocalizedValue(locale) : "")
        .thirdTextTranslations(
            advertisement.getThirdText() != null ? advertisement.getThirdText().getTranslations() :
                Collections.emptyMap())
        .imageUrl(advertisement.getImage().getUrl())
        .link(advertisement.getLink().toString())
        .expirationDate(advertisement.getExpirationDate().getValue())
        .creationDate(advertisement.getCreationDate().getValue())
        .lastModificationDate(advertisement.getLastModificationDate().getValue())
        .build();
  }
}
