package com.surofu.madeinrussia.infrastructure.persistence.deliveryMethod;

import java.time.ZonedDateTime;

public interface DeliveryMethodView {
    Long getId();

    String getName();

    ZonedDateTime getCreationDate();

    ZonedDateTime getLastModificationDate();
}
