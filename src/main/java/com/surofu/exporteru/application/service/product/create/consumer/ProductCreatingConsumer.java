package com.surofu.exporteru.application.service.product.create.consumer;

import com.surofu.exporteru.core.service.product.operation.CreateProduct;

public interface ProductCreatingConsumer {
  void accept(Long productId, CreateProduct operation);
}
