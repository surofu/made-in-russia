package com.surofu.exporteru.infrastructure.persistence.product.characteristic;

import java.time.Instant;

public interface ProductCharacteristicWithTranslationsView {
    Long getId();

    String getName();

    String getNameTranslations();

    String getValue();

    String getValueTranslations();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
