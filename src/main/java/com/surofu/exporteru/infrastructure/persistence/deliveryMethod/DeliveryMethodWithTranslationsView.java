package com.surofu.exporteru.infrastructure.persistence.deliveryMethod;

import java.time.Instant;

public interface DeliveryMethodWithTranslationsView {
    Long getId();

    String getName();

    String getNameTranslations();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
