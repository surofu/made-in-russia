package com.surofu.exporteru.infrastructure.persistence.product.characteristic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;

public interface ProductCharacteristicWithTranslationsView {
  Long getId();

  String getName();

  String getNameTranslations();

  String getValue();

  String getValueTranslations();

  Instant getCreationDate();

  Instant getLastModificationDate();

  default Map<String, String> getNameTranslationsMap() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(getNameTranslations(), new TypeReference<>() {
      });
    } catch (Exception e) {
      return Map.of();
    }
  }

  default Map<String, String> getValueTranslationsMap() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(getValueTranslations(), new TypeReference<>() {
      });
    } catch (Exception e) {
      return Map.of();
    }
  }
}
