package com.surofu.madeinrussia.infrastructure.persistence.product.packageOption;

import java.math.BigDecimal;
import java.time.Instant;

public interface ProductPackageOptionWithTranslationsView {
    Long getId();

    String getName();

    String getNameTranslations();

    BigDecimal getPrice();

    String getPriceUnit();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
