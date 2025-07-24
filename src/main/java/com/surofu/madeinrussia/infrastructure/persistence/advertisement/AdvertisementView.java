package com.surofu.madeinrussia.infrastructure.persistence.advertisement;

import java.time.Instant;

public interface AdvertisementView {
    Long getId();

    String getTitle();

    String getSubtitle();

    String getThirdText();

    String getImageUrl();

    String getLink();

    Boolean getIsBig();

    Instant getExpirationDate();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
