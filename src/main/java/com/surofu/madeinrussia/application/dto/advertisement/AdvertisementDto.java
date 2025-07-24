package com.surofu.madeinrussia.application.dto.advertisement;

import com.surofu.madeinrussia.core.model.advertisement.Advertisement;
import com.surofu.madeinrussia.infrastructure.persistence.advertisement.AdvertisementView;
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

    private String imageUrl;

    private Boolean isBig;

    private ZonedDateTime expirationDate;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    public static AdvertisementDto of(Advertisement advertisement) {
        return AdvertisementDto.builder()
                .id(advertisement.getId())
                .title(advertisement.getTitle().toString())
                .subtitle(advertisement.getSubtitle().toString())
                .imageUrl(advertisement.getImage().toString())
                .isBig(advertisement.getIsBig().getValue())
                .expirationDate(advertisement.getExpirationDate() == null ? null : advertisement.getExpirationDate().getValue())
                .creationDate(advertisement.getCreationDate().getValue())
                .lastModificationDate(advertisement.getLastModificationDate().getValue())
                .build();
    }

    public static AdvertisementDto of(AdvertisementView view) {
        return AdvertisementDto.builder()
                .id(view.getId())
                .title(view.getTitle())
                .subtitle(view.getSubtitle())
                .imageUrl(view.getImageUrl())
                .isBig(view.getIsBig())
                .expirationDate(view.getExpirationDate() == null ? null : view.getExpirationDate().atZone(ZoneId.systemDefault()))
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}
