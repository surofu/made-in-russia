package com.surofu.exporteru.application.service.product.create.consumer;

import com.surofu.exporteru.core.service.product.operation.CreateProduct;

public interface ProductCreationConsumer {
  void accept(Long productId, CreateProduct operation);
}
