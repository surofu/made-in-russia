package com.surofu.exporteru.application.service.oldproduct;

import com.surofu.exporteru.application.cache.ProductCacheManager;
import com.surofu.exporteru.application.cache.ProductSummaryCacheManager;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.service.product.operation.CreateProduct;
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;
import com.surofu.exporteru.infrastructure.persistence.product.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class OldProductSavingService {

  private final JpaProductRepository productRepository;
  private final ProductSummaryCacheManager productSummaryCacheManager;
  private final ProductCacheManager productCacheManager;

  public CreateProduct.Result saveCreate(Product product) {
    try {
      productRepository.save(product);
      productRepository.flush();
      return CreateProduct.Result.success();
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return CreateProduct.Result.errorSavingProduct(e);
    } finally {
      productSummaryCacheManager.clearAll();
    }
  }

  public UpdateProduct.Result saveUpdate(Product product) {
    try {
      productRepository.save(product);
      return UpdateProduct.Result.success();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return UpdateProduct.Result.success();
    } finally {
      productSummaryCacheManager.clearAll();
      productCacheManager.clearById(product.getId());
    }
  }
}
