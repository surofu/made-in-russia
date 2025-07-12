package com.surofu.madeinrussia.infrastructure.persistence.product.productReview.productReviewMedia;

import com.surofu.madeinrussia.core.model.media.MediaType;
import com.surofu.madeinrussia.core.model.product.productReview.productReviewMedia.*;

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
