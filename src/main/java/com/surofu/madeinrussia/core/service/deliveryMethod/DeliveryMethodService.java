package com.surofu.madeinrussia.core.service.deliveryMethod;

import com.surofu.madeinrussia.core.service.deliveryMethod.operation.GetDeliveryMethodById;
import com.surofu.madeinrussia.core.service.deliveryMethod.operation.GetDeliveryMethods;

public interface DeliveryMethodService {
    GetDeliveryMethods.Result getDeliveryMethods(GetDeliveryMethods operation);

    GetDeliveryMethodById.Result getDeliveryMethodById(GetDeliveryMethodById operation);
}
