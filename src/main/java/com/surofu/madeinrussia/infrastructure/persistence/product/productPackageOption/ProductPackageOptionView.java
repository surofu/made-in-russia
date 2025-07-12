package com.surofu.madeinrussia.infrastructure.persistence.product.productPackageOption;

import com.surofu.madeinrussia.core.model.product.productPackageOption.*;

public interface ProductPackageOptionView {
    Long getId();

    ProductPackageOptionName getName();

    ProductPackageOptionPrice getPrice();

    ProductPackageOptionPriceUnit getPriceUnit();

    ProductPackageOptionCreationDate getCreationDate();

    ProductPackageOptionLastModificationDate getLastModificationDate();
}
