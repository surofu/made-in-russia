package com.surofu.exporteru.infrastructure.persistence.product;

import java.time.Instant;
import java.util.Map;

public interface ProductWithTranslationsView {
    Long getId();

    Long getUserId();

    String getArticleCode();

    String getTitle();

    Map<String, String> getTitleTranslations();

    String getMainDescription();

    Map<String, String> getMainDescriptionTranslations();

    String getFurtherDescription();

    Map<String, String> getFurtherDescriptionTranslations();

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
