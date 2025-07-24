package com.surofu.madeinrussia.infrastructure.persistence.product.vendorDetails;

import java.time.Instant;

public interface ProductVendorDetailsView {
    Long getId();

    String getMainDescription();

    String getFurtherDescription();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
