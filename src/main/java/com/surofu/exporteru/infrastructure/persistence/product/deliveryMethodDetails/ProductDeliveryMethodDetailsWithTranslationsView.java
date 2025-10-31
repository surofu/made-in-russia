package com.surofu.exporteru.infrastructure.persistence.product.deliveryMethodDetails;

import java.time.Instant;

public interface ProductDeliveryMethodDetailsWithTranslationsView {
    Long getId();

    String getName();

    String getNameTranslations();

    String getValue();

    String getValueTranslations();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
