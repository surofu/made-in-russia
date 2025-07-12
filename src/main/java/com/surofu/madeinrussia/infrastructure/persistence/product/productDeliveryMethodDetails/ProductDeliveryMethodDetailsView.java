package com.surofu.madeinrussia.infrastructure.persistence.product.productDeliveryMethodDetails;

import com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails.ProductDeliveryMethodDetailsCreationDate;
import com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails.ProductDeliveryMethodDetailsLastModificationDate;
import com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails.ProductDeliveryMethodDetailsName;
import com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails.ProductDeliveryMethodDetailsValue;

public interface ProductDeliveryMethodDetailsView {
    Long getId();

    ProductDeliveryMethodDetailsName getName();

    ProductDeliveryMethodDetailsValue getValue();

    ProductDeliveryMethodDetailsCreationDate getCreationDate();

    ProductDeliveryMethodDetailsLastModificationDate getLastModificationDate();
}
