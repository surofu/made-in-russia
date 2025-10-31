package com.surofu.exporteru.core.service.order;

import com.surofu.exporteru.core.service.order.operation.CreateOrder;

public interface OrderService {
    CreateOrder.Result createOrder(CreateOrder operation);
}
