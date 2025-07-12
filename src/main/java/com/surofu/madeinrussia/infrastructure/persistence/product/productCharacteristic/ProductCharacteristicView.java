package com.surofu.madeinrussia.infrastructure.persistence.product.productCharacteristic;

import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristicCreationDate;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristicLastModificationDate;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristicName;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristicValue;

public interface ProductCharacteristicView {
    Long getId();

    ProductCharacteristicName getName();

    ProductCharacteristicValue getValue();

    ProductCharacteristicCreationDate getCreationDate();

    ProductCharacteristicLastModificationDate getLastModificationDate();
}
