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

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    public static AdvertisementDto of(Advertisement advertisement) {
        return AdvertisementDto.builder()
                .id(advertisement.getId())
                .title(advertisement.getTitle().toString())
                .subtitle(advertisement.getSubtitle().toString())
                .imageUrl(advertisement.getImage().toString())
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
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}
