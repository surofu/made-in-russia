package com.surofu.exporteru.infrastructure.persistence.vendor.productCategory;

import java.time.Instant;

public interface VendorProductCategoryView {
    Long getId();

    String getName();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
