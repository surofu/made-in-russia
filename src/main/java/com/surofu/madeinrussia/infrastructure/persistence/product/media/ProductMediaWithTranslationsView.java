package com.surofu.madeinrussia.infrastructure.persistence.product.media;

import com.surofu.madeinrussia.core.model.media.MediaType;

import java.time.Instant;

public interface ProductMediaWithTranslationsView {
    Long getId();

    MediaType getMediaType();

    String getMimeType();

    Integer getPosition();

    String getUrl();

    String getAltText();

    String getAltTextTranslations();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
