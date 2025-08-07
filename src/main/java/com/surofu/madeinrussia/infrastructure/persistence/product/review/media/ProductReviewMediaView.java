package com.surofu.madeinrussia.infrastructure.persistence.product.review.media;

import com.surofu.madeinrussia.core.model.media.MediaType;
import com.surofu.madeinrussia.core.model.product.review.media.*;

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
