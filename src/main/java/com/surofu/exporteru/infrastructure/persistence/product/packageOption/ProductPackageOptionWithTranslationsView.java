package com.surofu.exporteru.infrastructure.persistence.product.packageOption;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public interface ProductPackageOptionWithTranslationsView {
    Long getId();

    String getName();

    String getNameTranslations();

    BigDecimal getPrice();

    String getPriceUnit();

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
}
