package com.surofu.madeinrussia.infrastructure.persistence.product.vendorDetails.media;

import com.surofu.madeinrussia.core.model.media.MediaType;

import java.time.Instant;

public interface ProductVendorDetailsMediaView {
    Long getId();

    MediaType getMediaType();

    String getUrl();

    String getAltText();

    Integer getPosition();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
