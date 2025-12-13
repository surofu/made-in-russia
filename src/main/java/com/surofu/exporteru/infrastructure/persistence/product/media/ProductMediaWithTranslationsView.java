package com.surofu.exporteru.infrastructure.persistence.product.media;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surofu.exporteru.core.model.media.MediaType;

import java.time.Instant;
import java.util.Map;

public interface ProductMediaWithTranslationsView {
    Long getId();

    MediaType getMediaType();

    String getMimeType();

    Integer getPosition();

    String getUrl();

    String getAltText();

    String getAltTextTranslations();

    Instant getCreationDate();

    Instant getLastModificationDate();

    default Map<String, String> getAltTextTranslationsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(getAltTextTranslations(), new TypeReference<>() {
            });
        } catch (Exception e) {
            return Map.of();
        }
    }
}
