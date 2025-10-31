package com.surofu.exporteru.infrastructure.persistence.product;

import java.time.Instant;

public interface ProductWithTranslationsView {
    Long getId();

    Long getUserId();

    String getArticleCode();

    String getTitle();

    String getTitleTranslations();

    String getMainDescription();

    String getMainDescriptionTranslations();

    String getFurtherDescription();

    String getFurtherDescriptionTranslations();

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
