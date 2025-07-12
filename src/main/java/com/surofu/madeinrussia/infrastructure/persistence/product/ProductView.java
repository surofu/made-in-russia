package com.surofu.madeinrussia.infrastructure.persistence.product;

import java.time.Instant;

public interface ProductView {
    Long getId();

    Long getUserId();

    String getArticleCode();

    String getTitle();

    String getMainDescription();

    String getFurtherDescription();

    String getPreviewImageUrl();

    Integer getMinimumOrderQuantity();

    Instant getDiscountExpirationDate();

    Instant getCreationDate();

    Instant getLastModificationDate();

    // Calculated

    Double getRating();

    Integer getReviewsCount();

    // External

    Long getCategoryId();
}
