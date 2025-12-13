package com.surofu.exporteru.infrastructure.persistence.product.packageOption;

import java.math.BigDecimal;
import java.time.Instant;

public interface ProductPackageOptionView {
    Long getId();

    String getName();

    BigDecimal getPrice();

    String getPriceUnit();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
