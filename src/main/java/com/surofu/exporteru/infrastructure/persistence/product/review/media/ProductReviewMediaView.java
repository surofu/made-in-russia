package com.surofu.exporteru.infrastructure.persistence.product.review.media;

import com.surofu.exporteru.core.model.media.MediaType;
import com.surofu.exporteru.core.model.product.review.media.*;

public interface ProductReviewMediaView {
    Long getId();

    MediaType getMediaType();

    ProductReviewMediaMimeType getMimeType();

    ProductReviewMediaMediaPosition getPosition();

    ProductReviewMediaUrl getUrl();

    ProductReviewMediaAltText getAltText();

    ProductReviewMediaCreationDate getCreationDate();

    ProductReviewMediaLastModificationDate getLastModificationDate();
}
