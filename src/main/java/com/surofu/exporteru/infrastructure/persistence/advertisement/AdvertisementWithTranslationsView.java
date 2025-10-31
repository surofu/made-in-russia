package com.surofu.exporteru.infrastructure.persistence.advertisement;

import java.time.Instant;

public interface AdvertisementWithTranslationsView {
    Long getId();

    String getTitle();

    String getTitleTranslations();

    String getSubtitle();

    String getSubtitleTranslations();

    String getThirdText();

    String getThirdTextTranslations();

    String getImageUrl();

    String getLink();

    Boolean getIsBig();

    Instant getExpirationDate();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
