package com.surofu.exporteru.infrastructure.persistence.vendor.country;

import java.time.Instant;

public interface VendorCountryView {
    Long getId();

    String getName();

    String getValue();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
