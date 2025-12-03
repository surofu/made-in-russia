package com.surofu.exporteru.application.service.product.create.consumer;

import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.service.product.operation.CreateProduct;

public interface ProductCreatingConsumer {
  void accept(Product product, CreateProduct operation);
}
