package com.surofu.madeinrussia.infrastructure.persistence.vendor.country;

import java.time.Instant;

public interface VendorCountryWithTranslationsView {
    Long getId();

    String getName();

    String getNameTranslations();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
