package com.surofu.exporteru.application.service.product.update.comsumer;

import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;

public interface ProductUpdatingConsumer {
  void accept(Product product, UpdateProduct operation);
}
