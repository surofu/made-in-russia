package com.surofu.exporteru.infrastructure.persistence.vendor.productCategory;

import java.time.Instant;

public interface VendorProductCategoryWithTranslationsView {
    Long getId();

    String getName();

    String getNameTranslations();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
