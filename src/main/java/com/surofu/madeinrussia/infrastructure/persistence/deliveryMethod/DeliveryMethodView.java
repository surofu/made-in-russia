package com.surofu.madeinrussia.infrastructure.persistence.deliveryMethod;

import java.time.Instant;

public interface DeliveryMethodView {
    Long getId();

    String getName();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
