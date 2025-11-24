package com.surofu.exporteru.infrastructure.persistence.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;

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

    default Map<String, String> getTitleTranslationsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(getTitleTranslations(), new TypeReference<>() {
            });
        } catch (Exception e) {
            return Map.of();
        }
    }

    default Map<String, String> getMainDescriptionTranslationsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(getMainDescriptionTranslations(), new TypeReference<>() {
            });
        } catch (Exception e) {
            return Map.of();
        }
    }

    default Map<String, String> getFurtherDescriptionTranslationsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(getFurtherDescriptionTranslations(), new TypeReference<>() {
            });
        } catch (Exception e) {
            return Map.of();
        }
    }
}
