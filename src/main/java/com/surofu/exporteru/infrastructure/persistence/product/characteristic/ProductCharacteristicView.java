package com.surofu.exporteru.infrastructure.persistence.product.characteristic;

import java.time.Instant;

public interface ProductCharacteristicView {
    Long getId();

    String getName();

    String getValue();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
