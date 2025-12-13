package com.surofu.exporteru.core.service.deliveryMethod;

import com.surofu.exporteru.core.service.deliveryMethod.operation.GetDeliveryMethodById;
import com.surofu.exporteru.core.service.deliveryMethod.operation.GetDeliveryMethods;

public interface DeliveryMethodService {
    GetDeliveryMethods.Result getDeliveryMethods(GetDeliveryMethods operation);

    GetDeliveryMethodById.Result getDeliveryMethodById(GetDeliveryMethodById operation);
}
