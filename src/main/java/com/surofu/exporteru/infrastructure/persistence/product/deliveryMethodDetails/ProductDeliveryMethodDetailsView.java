package com.surofu.exporteru.infrastructure.persistence.product.deliveryMethodDetails;

import java.time.Instant;

public interface ProductDeliveryMethodDetailsView {
    Long getId();

    String getName();

    String getValue();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
