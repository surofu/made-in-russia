package com.surofu.exporteru.application.dto.advertisement;

import com.surofu.exporteru.core.model.advertisement.Advertisement;
import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class AdvertisementDto implements Serializable {
  private Long id;
  private String title;
  private String subtitle;
  private String thirdText;
  private String imageUrl;
  private String link;
  private Boolean isBig;
  private ZonedDateTime expirationDate;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  public static AdvertisementDto of(Advertisement advertisement) {
    return AdvertisementDto.builder()
        .id(advertisement.getId())
        .title(
            advertisement.getTitle() != null ? advertisement.getTitle().getLocalizedValue() :
                "")
        .subtitle(advertisement.getSubtitle() != null ?
            advertisement.getSubtitle().getLocalizedValue() : "")
        .thirdText(advertisement.getThirdText() != null ?
            advertisement.getThirdText().getLocalizedValue() : "")
        .imageUrl(advertisement.getImage().getUrl())
        .link(advertisement.getLink().toString())
        .isBig(advertisement.getIsBig().getValue())
        .expirationDate(advertisement.getExpirationDate().getValue())
        .creationDate(advertisement.getCreationDate().getValue())
        .lastModificationDate(advertisement.getLastModificationDate().getValue())
        .build();
  }
}
