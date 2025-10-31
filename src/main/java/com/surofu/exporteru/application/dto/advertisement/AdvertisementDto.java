package com.surofu.exporteru.application.dto.advertisement;

import com.surofu.exporteru.infrastructure.persistence.advertisement.AdvertisementView;
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

    public static AdvertisementDto of(AdvertisementView view) {
        return AdvertisementDto.builder()
                .id(view.getId())
                .title(view.getTitle())
                .subtitle(view.getSubtitle())
                .thirdText(view.getThirdText())
                .imageUrl(view.getImageUrl())
                .link(view.getLink())
                .isBig(view.getIsBig())
                .expirationDate(view.getExpirationDate() == null ? null : view.getExpirationDate().atZone(ZoneId.systemDefault()))
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}
