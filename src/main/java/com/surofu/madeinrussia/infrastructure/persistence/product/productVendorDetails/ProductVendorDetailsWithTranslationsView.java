package com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails;

import java.time.Instant;

public interface ProductVendorDetailsWithTranslationsView {
    Long getId();

    String getMainDescription();

    String getMainDescriptionTranslations();

    String getFurtherDescription();

    String getFurtherDescriptionTranslations();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
