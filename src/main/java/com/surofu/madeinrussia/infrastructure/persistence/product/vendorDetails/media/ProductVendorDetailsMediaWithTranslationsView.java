package com.surofu.madeinrussia.infrastructure.persistence.product.vendorDetails.media;

import com.surofu.madeinrussia.core.model.media.MediaType;

import java.time.Instant;

public interface ProductVendorDetailsMediaWithTranslationsView {
    Long getId();

    MediaType getMediaType();

    String getUrl();

    String getAltText();

    String getAltTextTranslations();

    Integer getPosition();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
