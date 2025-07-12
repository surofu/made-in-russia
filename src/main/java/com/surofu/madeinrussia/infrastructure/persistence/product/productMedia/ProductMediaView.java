package com.surofu.madeinrussia.infrastructure.persistence.product.productMedia;

import com.surofu.madeinrussia.core.model.media.MediaType;
import com.surofu.madeinrussia.core.model.product.productMedia.*;

public interface ProductMediaView {
    Long getId();

    MediaType getMediaType();

    ProductMediaMimeType getMimeType();

    ProductMediaPosition getPosition();

    ProductMediaUrl getUrl();

    ProductMediaAltText getAltText();

    ProductMediaCreationDate getCreationDate();

    ProductMediaLastModificationDate getLastModificationDate();
}
