package com.surofu.madeinrussia.infrastructure.persistence.vendor.productCategory;

import java.time.Instant;

public interface VendorProductCategoryView {
    Long getId();

    String getName();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
