package com.surofu.madeinrussia.infrastructure.persistence.advertisement;

import java.time.Instant;

public interface AdvertisementView {
    Long getId();

    String getTitle();

    String getSubtitle();

    String getImageUrl();

    Boolean getIsBig();

    Instant getExpirationDate();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
