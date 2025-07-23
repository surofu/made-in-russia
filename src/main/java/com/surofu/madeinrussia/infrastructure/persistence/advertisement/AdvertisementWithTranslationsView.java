package com.surofu.madeinrussia.infrastructure.persistence.advertisement;

import java.time.Instant;

public interface AdvertisementWithTranslationsView {
    Long getId();

    String getTitle();

    String getTitleTranslations();

    String getSubtitle();

    String getSubtitleTranslations();

    String getImageUrl();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
